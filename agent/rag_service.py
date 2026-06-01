import os
import json
from typing import List, Dict, Any
from dotenv import load_dotenv
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.embeddings import DashScopeEmbeddings, OllamaEmbeddings
from langchain_openai import ChatOpenAI
import chromadb
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.documents import Document

load_dotenv()

DASHSCOPE_API_KEY = os.getenv("DASHSCOPE_API_KEY", "sk-c7604bb0ad6640eb84dec591c42ce0b0")
OPENAI_BASE_URL = os.getenv("OPENAI_BASE_URL", "https://dashscope.aliyuncs.com/compatible-mode/v1")
OPENAI_MODEL_NAME = os.getenv("OPENAI_MODEL_NAME", "qwen3.6-plus")
CHUNK_SIZE = int(os.getenv("CHUNK_SIZE", 1000))
CHUNK_OVERLAP = int(os.getenv("CHUNK_OVERLAP", 50))
TOP_K = int(os.getenv("TOP_K", 3))
PERSIST_DIRECTORY = "./chroma_db"
# 持久化文件夹向量存储的位置可以包含多个表所以还有一层文件夹

embeddings = OllamaEmbeddings(
    model="qwen3-embedding:0.6b",
    base_url="http://127.0.0.1:11434"
)
client = chromadb.PersistentClient(path=PERSIST_DIRECTORY)
collection= client.get_or_create_collection(name="rag_collection")

llm = ChatOpenAI(
    model=OPENAI_MODEL_NAME,
    temperature=0.7,
    openai_api_key=DASHSCOPE_API_KEY,
    openai_api_base=OPENAI_BASE_URL
)

CONVERSATION_FILE = "./conversation_history.json"

conversation_history: List[Dict[str, str]] = []
query_count: int = 0

def load_conversation_history():
    global conversation_history
    try:
        if os.path.exists(CONVERSATION_FILE):
            with open(CONVERSATION_FILE, 'r', encoding='utf-8') as f:
                conversation_history = json.load(f)
        else:
            conversation_history = []
    except Exception as e:
        print(f"加载对话历史失败: {str(e)}")
        conversation_history = []

def save_conversation_history():
    try:
        with open(CONVERSATION_FILE, 'w', encoding='utf-8') as f:
            json.dump(conversation_history, f, ensure_ascii=False, indent=2)
    except Exception as e:
        print(f"保存对话历史失败: {str(e)}")

load_conversation_history()

CHAT_HISTORY_FILE = "./chat_history.json"
chat_history: List[Dict[str, str]] = []

def load_chat_history():
    global chat_history
    try:
        if os.path.exists(CHAT_HISTORY_FILE):
            with open(CHAT_HISTORY_FILE, 'r', encoding='utf-8') as f:
                chat_history = json.load(f)
        else:
            chat_history = []
    except Exception as e:
        print(f"加载聊天历史失败: {str(e)}")
        chat_history = []

def save_chat_history():
    try:
        with open(CHAT_HISTORY_FILE, 'w', encoding='utf-8') as f:
            json.dump(chat_history, f, ensure_ascii=False, indent=2)
    except Exception as e:
        print(f"保存聊天历史失败: {str(e)}")

def add_chat_message(role: str, content: str):
    global chat_history
    chat_history.append({"role": role, "content": content})
    if len(chat_history) > 20:
        chat_history.pop(0)
    save_chat_history()

def get_chat_history() -> List[Dict[str, str]]:
    return chat_history

def clear_chat_history():
    global chat_history
    chat_history = []
    save_chat_history()

load_chat_history()

interview_state = {
    "is_active": False,
    "resume_content": "",
    "questions": [],
    "current_question_index": 0,
    "current_question": "",
    "current_answer": "",
    "evaluations": []
}

PROMPT_TEMPLATE = """你是一个专业的智能助手。请根据以下信息回答用户的问题：

对话历史：
{history}

参考信息：
{context}

用户问题：{question}

请基于参考信息和对话历史回答问题，如果参考信息中没有答案，请说明。回答要准确、简洁。
"""

INTERVIEW_QUESTION_PROMPT = """
你是一位资深的技术面试官，擅长深挖项目细节。现在需要为一位求职者生成模拟面试问题。

{resume_content}

面试流程：
1. 第一题必须是自我介绍
2. 后面的问题必须根据简历中提到的【项目】和【技术栈】进行深挖
3. 每个问题都要结合简历中提到的具体场景、遇到的困难、解决方案来提问
4. 技术问题要问"怎么做"的深层原理，不只是"是什么"

例如：
- 如果简历提到Redis → 问缓存击穿/穿透/雪崩、数据一致性、过期策略
- 如果简历提到高并发 → 问如何保证高可用、限流熔断、接口优化
- 如果简历提到数据库 → 问索引原理、事务隔离级别、慢查询优化
- 如果简历提到项目难点 → 问具体解决方案、技术选型理由

请以JSON格式输出，包含"questions"数组，每个问题包含：
- "question": 面试问题
- "answer": 参考答案（要详细，包含原理说明）
- "category": 问题类别（自我介绍/项目深挖/技术原理/解决方案）

确保生成8-10个问题，问题之间有逻辑递进关系。
"""

EVALUATION_PROMPT = """
面试问题：{question}
用户回答：{answer}
正确答案：{correct_answer}

请直接给出评估，格式：
评分：X分（简短评价）
正确答案：{correct_answer}

保持简洁，不超过50字。
"""

def process_pdf(file_path: str) -> dict:
    try:
        file_name = os.path.basename(file_path)
        
        existing_docs = collection.get(limit=1, where={"source": file_name})
        
        if existing_docs and existing_docs.get('ids') and len(existing_docs['ids']) > 0:
            return {
                "success": False,
                "exists": True,
                "message": f"文件 {file_name} 已存在，无需重复上传"
            }
        
        all_ids = collection.get(limit=10000)
        if all_ids and all_ids.get('ids'):
            for doc_id in all_ids['ids']:
                if file_name in doc_id:
                    return {
                        "success": False,
                        "exists": True,
                        "message": f"文件 {file_name} 已存在，无需重复上传"
                    }
        
        loader = PyPDFLoader(file_path)
        documents = loader.load()
        
        if not documents or all(len(doc.page_content.strip()) == 0 for doc in documents):
            return {
                "success": False,
                "exists": False,
                "message": "PDF文档内容为空，无法处理"
            }
        
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=CHUNK_SIZE,
            chunk_overlap=CHUNK_OVERLAP,
            length_function=len
        )
        chunks = text_splitter.split_documents(documents)
        
        if chunks:
            texts = [doc.page_content for doc in chunks]
            metadatas = []
            for doc in chunks:
                metadata = doc.metadata.copy()
                metadata['source'] = file_name
                metadatas.append(metadata)

            embeddings_list = embeddings.embed_documents(texts)
            
            ids = [f"doc_{i}_{file_name}_{hash(texts[i]) % 1000000}" for i in range(len(chunks))]
            
            collection.add(
                ids=ids,
                embeddings=embeddings_list,
                documents=texts,
                metadatas=metadatas
            )
            return {
                "success": True,
                "exists": False,
                "message": f"文件 {file_name} 上传并处理成功"
            }
        return {
            "success": False,
            "exists": False,
            "message": "PDF文档分割失败"
        }
    except Exception as e:
        print(f"PDF处理错误: {str(e)}")
        return {
            "success": False,
            "exists": False,
            "message": f"PDF处理错误: {str(e)}"
        }

def similarity_search(query: str) -> List[Document]:
    try:
        query_embedding = embeddings.embed_query(query)
        
        results = collection.query(
            query_embeddings=[query_embedding],
            n_results=TOP_K
        )
        
        documents = []
        if results['documents'] and len(results['documents']) > 0:
            for i, text in enumerate(results['documents'][0]):
                metadata = {}
                if results['metadatas'] and len(results['metadatas']) > 0:
                    metadata = results['metadatas'][0][i]
                doc = Document(page_content=text, metadata=metadata)
                documents.append(doc)
        
        return documents
    except Exception as e:
        print(f"相似度查询错误: {str(e)}")
        return []

def format_history(history: List[Dict[str, str]]) -> str:
    if not history:
        return "无"
    formatted = []
    for msg in history:
        role = "用户" if msg["role"] == "user" else "助手"
        formatted.append(f"{role}: {msg['content']}")
    return "\n".join(formatted)

def query_with_rag(question: str) -> Dict[str, Any]:
    global query_count
    try:
        query_count += 1
        context_docs = similarity_search(question)
        
        context_str = "\n\n".join([f"段落{i+1}: {doc.page_content}" for i, doc in enumerate(context_docs)])
        
        history_str = format_history(conversation_history)
        
        prompt = ChatPromptTemplate.from_template(PROMPT_TEMPLATE)
        chain = prompt | llm
        
        response = chain.invoke({
            "history": history_str,
            "context": context_str,
            "question": question
        })
        
        answer = response.content
        
        conversation_history.append({"role": "user", "content": question})
        conversation_history.append({"role": "assistant", "content": answer})
        
        if len(conversation_history) > 20:
            conversation_history.pop(0)
            conversation_history.pop(0)
        
        save_conversation_history()
        
        return {
            "answer": answer,
            "sources": [doc.page_content for doc in context_docs]
        }
    except Exception as e:
        print(f"查询错误: {str(e)}")
        return {
            "answer": "抱歉，我遇到了一些问题，请稍后再试。",
            "sources": []
        }

def clear_history():
    global conversation_history
    conversation_history = []
    save_conversation_history()

def get_history() -> List[Dict[str, str]]:
    return conversation_history

def query_with_rag_chat(question: str) -> Dict[str, Any]:
    global query_count, chat_history
    try:
        query_count += 1
        context_docs = similarity_search(question)
        
        context_str = "\n\n".join([f"段落{i+1}: {doc.page_content}" for i, doc in enumerate(context_docs)])
        
        history_str = format_history(chat_history)
        
        prompt = ChatPromptTemplate.from_template(PROMPT_TEMPLATE)
        chain = prompt | llm
        
        response = chain.invoke({
            "history": history_str,
            "context": context_str,
            "question": question
        })
        
        answer = response.content
        
        chat_history.append({"role": "user", "content": question})
        chat_history.append({"role": "assistant", "content": answer})
        
        if len(chat_history) > 20:
            chat_history.pop(0)
            chat_history.pop(0)
        
        save_chat_history()
        
        return {
            "answer": answer,
            "sources": [doc.page_content for doc in context_docs]
        }
    except Exception as e:
        print(f"查询错误: {str(e)}")
        return {
            "answer": "抱歉，我遇到了一些问题，请稍后再试。",
            "sources": []
        }

def get_chat_history_data() -> List[Dict[str, str]]:
    return chat_history

def clear_chat_history_data():
    global chat_history
    chat_history = []
    save_chat_history()

def start_interview(resume_path: str = "") -> str:
    global interview_state
    
    resume_content = ""
    
    if resume_path and os.path.exists(resume_path):
        try:
            loader = PyPDFLoader(resume_path)
            documents = loader.load()
            resume_content = "\n\n".join([doc.page_content for doc in documents])
        except Exception as e:
            print(f"读取简历失败: {str(e)}")
            resume_content = ""
    
    if not resume_content:
        resume_content = "用户简历：\n- 熟练掌握Java编程\n- 熟悉Spring Boot框架\n- 有数据库设计经验\n- 参与过多个企业级项目开发"
    
    interview_state["is_active"] = True
    interview_state["resume_content"] = resume_content
    interview_state["questions"] = []
    interview_state["current_question_index"] = 0
    interview_state["current_question"] = ""
    interview_state["current_answer"] = ""
    interview_state["evaluations"] = []
    
    try:
        prompt = ChatPromptTemplate.from_template(INTERVIEW_QUESTION_PROMPT)
        chain = prompt | llm
        
        response = chain.invoke({
            "resume_content": resume_content
        })
        
        import json
        result = json.loads(response.content)
        
        if "questions" in result and isinstance(result["questions"], list):
            interview_state["questions"] = result["questions"]
        else:
            interview_state["questions"] = [
                {"question": "请做一个2-3分钟的自我介绍，包括你的教育背景、技术栈和做过的项目", "answer": "我叫XXX，毕业于XX大学XX专业。从事前端/后端开发X年，熟练掌握Java/Spring Boot/Vue等技术栈。我做过X个项目，其中最印象深刻的是XX项目，主要解决了XX问题，使用了XX技术方案。", "category": "自我介绍"},
                {"question": "请介绍一下你简历中提到的Redis项目，具体用Redis做了什么？", "answer": "在我们的XX系统中，我们使用Redis作为缓存层，主要用于：1）热点数据缓存，减少数据库压力；2）分布式Session共享；3）接口限流。Redis的高性能帮助我们将接口响应时间从200ms降低到20ms。", "category": "项目深挖"},
                {"question": "你提到项目用了Redis，那Redis是怎么实现缓存击穿、穿透和雪崩的？你们项目是怎么处理的？", "answer": "缓存击穿：热点key过期时，大量请求同时打到数据库。解决方案：使用互斥锁或永久不过期+异步更新。\n缓存穿透：请求的数据既不在缓存也不在数据库。解决方案：布隆过滤器或缓存空值。\n缓存雪崩：大量缓存同时过期。解决方案：过期时间加随机值，或使用永久缓存+定时更新。", "category": "技术原理"},
                {"question": "Redis的持久化机制你们用的哪种？RDB和AOF有什么区别？你们项目选型依据是什么？", "answer": "RDB是定时快照，AOF是记录每次写操作。我们项目用的是RDB+AOF混合持久化，兼顾性能和数据安全。AOF优先保证数据不丢失，RDB做定期备份。生产环境推荐AOF everysec，兼顾性能和数据安全。", "category": "技术原理"},
                {"question": "在你做的项目中，遇到过的最大技术挑战是什么？你是怎么解决的？", "answer": "最大的挑战是XX系统的高并发问题。原来单机QPS只有100，我们通过以下方案优化到1000+：1）Redis缓存热点数据；2）数据库读写分离+分库分表；3）接口异步化；4）使用消息队列削峰。最终将系统吞吐量提升了10倍。", "category": "解决方案"},
                {"question": "你简历提到使用了MySQL，能讲一下MySQL索引的底层数据结构吗？B+树相比B树有什么优势？", "answer": "MySQL索引底层使用B+树。B+树相比B树的优势：1）非叶子节点不存储数据，只存储索引，可以容纳更多索引，树的深度更小；2）所有数据都存在叶子节点，且叶子节点之间用链表相连，范围查询更快；3）查询效率更稳定，所有查询都要到叶子节点。", "category": "技术原理"},
                {"question": "MySQL事务的隔离级别有哪些？你们项目用的哪个？为什么？", "answer": "MySQL有4个隔离级别：Read Uncommitted、Read Committed、Repeatable Read、Serializable。我们项目用的是Repeatable Read（MySQL默认），因为它可以避免脏读和不可重复读，满足大部分业务需求，且性能比Serializable好。对于强一致性的场景会用Serializable。", "category": "技术原理"},
                {"question": "你们项目是怎么做接口优化的？有没有具体的案例？", "answer": "我们从以下几个方面优化：1）前端：减少请求次数，使用防抖节流；2）后端：Redis缓存热点数据、接口异步化；3）数据库：创建合理索引、SQL优化、分库分表；4）架构：CDN加速、静态资源分离。比如首页接口从500ms优化到50ms。", "category": "解决方案"},
            ]
        
        if interview_state["questions"]:
            interview_state["current_question"] = interview_state["questions"][0]["question"]
            interview_state["current_answer"] = interview_state["questions"][0]["answer"]
            return f"面试官：你好，请坐。我看了你的简历，现在开始面试。\n\n第一题（自我介绍）：{interview_state['current_question']}"
        else:
            return "面试问题生成失败，请重试。"
    
    except Exception as e:
        print(f"生成面试问题失败: {str(e)}")
        interview_state["questions"] = [
            {"question": "请做一个2-3分钟的自我介绍，包括你的教育背景、技术栈和做过的项目", "answer": "我叫XXX，毕业于XX大学XX专业。从事前端/后端开发X年，熟练掌握Java/Spring Boot/Vue等技术栈。我做过X个项目，其中最印象深刻的是XX项目，主要解决了XX问题，使用了XX技术方案。", "category": "自我介绍"},
            {"question": "请介绍一下你简历中提到的Redis项目，具体用Redis做了什么？", "answer": "在我们的XX系统中，我们使用Redis作为缓存层，主要用于：1）热点数据缓存，减少数据库压力；2）分布式Session共享；3）接口限流。Redis的高性能帮助我们将接口响应时间从200ms降低到20ms。", "category": "项目深挖"},
            {"question": "你提到项目用了Redis，那Redis是怎么实现缓存击穿、穿透和雪崩的？你们项目是怎么处理的？", "answer": "缓存击穿：热点key过期时，大量请求同时打到数据库。解决方案：使用互斥锁或永久不过期+异步更新。\n缓存穿透：请求的数据既不在缓存也不在数据库。解决方案：布隆过滤器或缓存空值。\n缓存雪崩：大量缓存同时过期。解决方案：过期时间加随机值，或使用永久缓存+定时更新。", "category": "技术原理"},
            {"question": "Redis的持久化机制你们用的哪种？RDB和AOF有什么区别？你们项目选型依据是什么？", "answer": "RDB是定时快照，AOF是记录每次写操作。我们项目用的是RDB+AOF混合持久化，兼顾性能和数据安全。AOF优先保证数据不丢失，RDB做定期备份。生产环境推荐AOF everysec，兼顾性能和数据安全。", "category": "技术原理"},
            {"question": "在你做的项目中，遇到过的最大技术挑战是什么？你是怎么解决的？", "answer": "最大的挑战是XX系统的高并发问题。原来单机QPS只有100，我们通过以下方案优化到1000+：1）Redis缓存热点数据；2）数据库读写分离+分库分表；3）接口异步化；4）使用消息队列削峰。最终将系统吞吐量提升了10倍。", "category": "解决方案"},
            {"question": "你简历提到使用了MySQL，能讲一下MySQL索引的底层数据结构吗？B+树相比B树有什么优势？", "answer": "MySQL索引底层使用B+树。B+树相比B树的优势：1）非叶子节点不存储数据，只存储索引，可以容纳更多索引，树的深度更小；2）所有数据都存在叶子节点，且叶子节点之间用链表相连，范围查询更快；3）查询效率更稳定，所有查询都要到叶子节点。", "category": "技术原理"},
            {"question": "MySQL事务的隔离级别有哪些？你们项目用的哪个？为什么？", "answer": "MySQL有4个隔离级别：Read Uncommitted、Read Committed、Repeatable Read、Serializable。我们项目用的是Repeatable Read（MySQL默认），因为它可以避免脏读和不可重复读，满足大部分业务需求，且性能比Serializable好。对于强一致性的场景会用Serializable。", "category": "技术原理"},
            {"question": "你们项目是怎么做接口优化的？有没有具体的案例？", "answer": "我们从以下几个方面优化：1）前端：减少请求次数，使用防抖节流；2）后端：Redis缓存热点数据、接口异步化；3）数据库：创建合理索引、SQL优化、分库分表；4）架构：CDN加速、静态资源分离。比如首页接口从500ms优化到50ms。", "category": "解决方案"},
        ]
        interview_state["current_question"] = interview_state["questions"][0]["question"]
        interview_state["current_answer"] = interview_state["questions"][0]["answer"]
        return f"面试官：你好，请坐。我看了你的简历，现在开始面试。\n\n第一题（自我介绍）：{interview_state['current_question']}"

def ask_interview_question() -> str:
    global interview_state
    
    if not interview_state["is_active"]:
        return "请先开始模拟面试。"
    
    index = interview_state["current_question_index"]
    
    if index >= len(interview_state["questions"]):
        return "面试已结束！感谢你的参与。"
    
    interview_state["current_question"] = interview_state["questions"][index]["question"]
    interview_state["current_answer"] = interview_state["questions"][index]["answer"]
    
    return interview_state["current_question"]

def evaluate_answer(answer: str) -> Dict[str, Any]:
    global interview_state
    
    if not interview_state["is_active"]:
        return {"evaluation": "请先开始模拟面试。", "is_last": True}
    
    answer_lower = answer.lower().strip()
    
    if "退出" in answer or "不面了" in answer or "结束面试" in answer:
        interview_state["is_active"] = False
        return {
            "evaluation": "面试已提前结束，感谢你的参与！",
            "correct_answer": "",
            "is_last": True,
            "questionNumber": interview_state["current_question_index"],
            "totalQuestions": len(interview_state["questions"]),
            "nextQuestion": ""
        }
    
    question = interview_state["current_question"]
    correct_answer = interview_state["current_answer"]
    
    if "不会" in answer or "不知道" in answer or "不清楚" in answer:
        rag_result = query_with_rag(question)
        correct_answer = rag_result["answer"]
        evaluation = f"评分：0分\n\n这个问题可以这样理解：{correct_answer[:100]}..."
    else:
        try:
            prompt = ChatPromptTemplate.from_template(EVALUATION_PROMPT)
            chain = prompt | llm
            
            response = chain.invoke({
                "question": question,
                "answer": answer,
                "correct_answer": correct_answer
            })
            
            evaluation = response.content
        except Exception as e:
            print(f"评估回答失败: {str(e)}")
            evaluation = f"评分：5分\n\n正确答案：{correct_answer}"
    
    interview_state["evaluations"].append({
        "question": question,
        "user_answer": answer,
        "correct_answer": correct_answer,
        "evaluation": evaluation
    })
    
    interview_state["current_question_index"] += 1
    
    if interview_state["current_question_index"] >= 5:
        interview_state["is_active"] = False
        return {
            "evaluation": evaluation,
            "correct_answer": correct_answer,
            "is_last": True,
            "questionNumber": interview_state["current_question_index"],
            "totalQuestions": len(interview_state["questions"]),
            "nextQuestion": ""
        }
    
    is_last = interview_state["current_question_index"] >= len(interview_state["questions"])
    
    next_question = ""
    if not is_last:
        interview_state["current_question"] = interview_state["questions"][interview_state["current_question_index"]]["question"]
        interview_state["current_answer"] = interview_state["questions"][interview_state["current_question_index"]]["answer"]
        next_question = interview_state["current_question"]
    
    return {
        "evaluation": evaluation,
        "correct_answer": correct_answer,
        "is_last": is_last,
        "questionNumber": interview_state["current_question_index"],
        "totalQuestions": len(interview_state["questions"]),
        "nextQuestion": next_question
    }

def get_interview_state() -> Dict[str, Any]:
    return {
        "is_active": interview_state["is_active"],
        "current_question_index": interview_state["current_question_index"],
        "total_questions": len(interview_state["questions"]),
        "current_question": interview_state["current_question"]
    }

def get_statistics() -> Dict[str, Any]:
    try:
        upload_dir = "uploads"
        uploaded_files = 0
        if os.path.exists(upload_dir):
            for filename in os.listdir(upload_dir):
                file_path = os.path.join(upload_dir, filename)
                if os.path.isfile(file_path) and filename.lower().endswith('.pdf'):
                    uploaded_files += 1
        
        knowledge_chunks = 0
        try:
            all_docs = collection.get(limit=10000)
            if all_docs and all_docs.get('ids'):
                knowledge_chunks = len(all_docs['ids'])
        except Exception as e:
            print(f"获取知识片段数量错误: {str(e)}")
        
        search_count = query_count
        
        return {
            "uploaded_files": uploaded_files,
            "knowledge_chunks": knowledge_chunks,
            "search_count": search_count
        }
    except Exception as e:
        print(f"获取统计数据错误: {str(e)}")
        return {
            "uploaded_files": 0,
            "knowledge_chunks": 0,
            "search_count": 0
        }

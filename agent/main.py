import os
import shutil
import uuid
from fastapi import FastAPI, File, UploadFile, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import List, Dict, Any
import uvicorn
from rag_service import process_pdf, query_with_rag, clear_history, get_history, start_interview, ask_interview_question, evaluate_answer, get_interview_state, get_statistics, query_with_rag_chat, get_chat_history_data, clear_chat_history_data

app = FastAPI(title="RAG智能助手")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "uploads"
RESUME_DIR = os.path.join(UPLOAD_DIR, "resumes")
os.makedirs(UPLOAD_DIR, exist_ok=True)
os.makedirs(RESUME_DIR, exist_ok=True)
os.makedirs("static", exist_ok=True)

print(f"上传目录: {os.path.abspath(RESUME_DIR)}")

class QueryRequest(BaseModel):
    question: str

class DeleteRequest(BaseModel):
    userId: str
    fileName: str

class InterviewRequest(BaseModel):
    userId: str
    answer: str = ""

@app.get("/")
async def root():
    return FileResponse("static/index.html")

@app.post("/api/upload")
async def upload_pdf(file: UploadFile = File(...)):
    try:
        if not file.filename.endswith(".pdf"):
            raise HTTPException(status_code=400, detail="只支持PDF文件")
        
        file_path = os.path.join(UPLOAD_DIR, file.filename)
        
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        result = process_pdf(file_path)
        
        if result.get("success"):
            return {
                "success": True, 
                "exists": False,
                "message": result.get("message", "上传并处理成功")
            }
        elif result.get("exists"):
            return {
                "success": True, 
                "exists": True,
                "message": result.get("message", "文件已存在")
            }
        else:
            raise HTTPException(status_code=500, detail=result.get("message", "PDF处理失败"))
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/upload")
async def upload_resume(
    file: UploadFile = File(...), 
    userId: str = Form("1"), 
    type: str = Form("resume"), 
    fileName: str = Form("")
):
    try:
        print(f"收到上传请求: userId={userId}, originalFilename={file.filename}, userFileName={fileName}")
        
        use_filename = fileName if fileName else file.filename
        if not use_filename.lower().endswith(".pdf"):
            use_filename = use_filename + ".pdf"
        
        user_dir = os.path.join(RESUME_DIR, userId)
        os.makedirs(user_dir, exist_ok=True)
        print(f"用户目录: {os.path.abspath(user_dir)}")
        
        file_path = os.path.join(user_dir, use_filename)
        
        counter = 1
        base_name, ext = os.path.splitext(use_filename)
        while os.path.exists(file_path):
            use_filename = f"{base_name}_{counter}{ext}"
            file_path = os.path.join(user_dir, use_filename)
            counter += 1
        
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        print(f"文件已保存: {os.path.abspath(file_path)}")
        print(f"目录文件列表: {os.listdir(user_dir)}")
        
        return {"success": True, "message": "上传成功", "filename": use_filename}
    except Exception as e:
        print(f"上传错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.get("/files")
async def get_files(userId: str = "1", type: str = "resume"):
    try:
        print(f"获取文件列表: userId={userId}")
        
        user_dir = os.path.join(RESUME_DIR, userId)
        os.makedirs(user_dir, exist_ok=True)
        print(f"用户目录: {os.path.abspath(user_dir)}")
        
        if os.path.exists(user_dir):
            print(f"目录内容: {os.listdir(user_dir)}")
        else:
            print(f"目录不存在")
        
        files = []
        if os.path.exists(user_dir):
            for filename in os.listdir(user_dir):
                print(f"找到文件: {filename}")
                if filename.lower().endswith(".pdf"):
                    file_path = os.path.join(user_dir, filename)
                    file_size = os.path.getsize(file_path)
                    
                    if file_size < 1024:
                        size_str = f"{file_size} B"
                    elif file_size < 1024 * 1024:
                        size_str = f"{file_size / 1024:.2f} KB"
                    else:
                        size_str = f"{file_size / (1024 * 1024):.2f} MB"
                    
                    files.append({
                        "name": filename,
                        "size": size_str,
                        "url": f"/download/{userId}/{filename}"
                    })
        
        print(f"返回文件列表: {files}")
        return {"success": True, "files": files}
    except Exception as e:
        print(f"获取文件列表错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.get("/download/{userId}/{filename}")
async def download_file(userId: str, filename: str):
    try:
        file_path = os.path.join(RESUME_DIR, userId, filename)
        print(f"下载文件: {os.path.abspath(file_path)}")
        
        if not os.path.exists(file_path):
            raise HTTPException(status_code=404, detail="文件不存在")
        
        return FileResponse(file_path, filename=filename)
    except HTTPException:
        raise
    except Exception as e:
        print(f"下载错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/delete")
async def delete_file(request: DeleteRequest):
    try:
        file_path = os.path.join(RESUME_DIR, request.userId, request.fileName)
        print(f"删除文件: {os.path.abspath(file_path)}")
        
        if not os.path.exists(file_path):
            return {"success": False, "message": "文件不存在"}
        
        os.remove(file_path)
        return {"success": True, "message": "删除成功"}
    except Exception as e:
        print(f"删除错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.post("/api/query")
async def query(request: QueryRequest):
    try:
        question = request.question.lower()
        if "模拟面试" in question:
            result = start_interview()
            return {"success": True, "data": {"answer": result, "type": "interview_start"}}
        result = query_with_rag(request.question)
        return {"success": True, "data": result}
    except Exception as e:
        print(f"查询错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/interview/start")
async def api_start_interview(userId: str = Form("1")):
    try:
        user_dir = os.path.join(RESUME_DIR, userId)
        resume_files = []
        if os.path.exists(user_dir):
            resume_files = [f for f in os.listdir(user_dir) if f.lower().endswith(".pdf")]
        
        if not resume_files:
            result = start_interview()
        else:
            resume_path = os.path.join(user_dir, resume_files[0])
            result = start_interview(resume_path)
        return {"success": True, "data": {"answer": result, "type": "interview_start"}}
    except Exception as e:
        print(f"开始面试错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.post("/api/interview/next")
async def api_next_question():
    try:
        result = ask_interview_question()
        return {"success": True, "data": {"question": result, "type": "interview_question"}}
    except Exception as e:
        print(f"获取下一题错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.post("/api/interview/evaluate")
async def api_evaluate_answer(answer: str = Form("")):
    try:
        result = evaluate_answer(answer)
        return {"success": True, "data": {
            "evaluation": result["evaluation"],
            "isLast": result["is_last"],
            "questionNumber": result["questionNumber"],
            "totalQuestions": result["totalQuestions"],
            "nextQuestion": result["nextQuestion"],
            "type": "interview_evaluation"
        }}
    except Exception as e:
        print(f"评估回答错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.get("/api/interview/state")
async def api_get_interview_state():
    try:
        state = get_interview_state()
        return {"success": True, "data": state}
    except Exception as e:
        print(f"获取面试状态错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        return {"success": False, "message": str(e)}

@app.get("/api/history")
async def get_conversation_history():
    try:
        history = get_history()
        return {"success": True, "data": history}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/history/clear")
async def clear_conversation_history():
    try:
        clear_history()
        return {"success": True, "message": "对话历史已清空"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/chat/query")
async def chat_query(request: QueryRequest):
    try:
        result = query_with_rag_chat(request.question)
        return {"success": True, "data": result}
    except Exception as e:
        print(f"聊天查询错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/chat/history")
async def get_chat_history():
    try:
        history = get_chat_history_data()
        return {"success": True, "data": history}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/api/chat/history/clear")
async def clear_chat_history():
    try:
        clear_chat_history_data()
        return {"success": True, "message": "聊天历史已清空"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/api/statistics")
async def get_statistics_data():
    try:
        stats = get_statistics()
        return {"success": True, "data": stats}
    except Exception as e:
        print(f"获取统计数据错误: {str(e)}")
        import traceback
        print(traceback.format_exc())
        raise HTTPException(status_code=500, detail=str(e))

app.mount("/static", StaticFiles(directory="static"), name="static")

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8001)
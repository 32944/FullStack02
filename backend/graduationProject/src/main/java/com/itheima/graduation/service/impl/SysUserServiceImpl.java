package com.itheima.graduation.service.impl;

import com.itheima.graduation.constant.UserConstant;
import com.itheima.graduation.dto.UserDTO;
import com.itheima.graduation.entity.PageResult;
import com.itheima.graduation.entity.SysUser;
import com.itheima.graduation.exception.BusinessException;
import com.itheima.graduation.mapper.SysUserMapper;
import com.itheima.graduation.service.SysUserService;
import com.itheima.graduation.service.UserClockService;
import com.itheima.graduation.service.UserFavoriteService;
import com.itheima.graduation.service.UserWrongService;
import com.itheima.graduation.vo.UserDetailVO;
import com.itheima.graduation.vo.UserVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserFavoriteService userFavoriteService;

    @Autowired
    private UserWrongService userWrongService;

    @Autowired
    private UserClockService userClockService;

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void add(UserDTO userDTO) {
        if (userDTO.getOpenid() == null || userDTO.getOpenid().trim().isEmpty()) {
            throw new BusinessException("openid不能为空");
        }

        SysUser existUser = sysUserMapper.selectByOpenid(userDTO.getOpenid());
        if (existUser != null) {
            throw new BusinessException("用户已存在");
        }

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDTO, sysUser);

        if (sysUser.getStatus() == null) {
            sysUser.setStatus(UserConstant.STATUS_NORMAL);
        }
        if (sysUser.getCurrentLevel() == null) {
            sysUser.setCurrentLevel(1);
        }

        sysUserMapper.insert(sysUser);
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void update(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        String lockKey = "user:update:" + userDTO.getId();
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }

            SysUser existUser = sysUserMapper.selectById(userDTO.getId());
            if (existUser == null) {
                throw new BusinessException("用户不存在");
            }

            SysUser sysUser = new SysUser();
            BeanUtils.copyProperties(userDTO, sysUser);
            sysUserMapper.updateById(sysUser);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void delete(Long id) {
        String lockKey = "user:delete:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }
            sysUserMapper.deleteById(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Cacheable(value = "user", key = "'getById:' + #id")
    public UserVO getById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(sysUser);
    }

    @Override
    public PageResult<UserVO> page(UserDTO userDTO) {
        Integer offset = (userDTO.getPageNum() - 1) * userDTO.getPageSize();

        List<SysUser> userList = sysUserMapper.selectPage(
                userDTO.getNickname(),
                userDTO.getTargetJob(),
                userDTO.getStatus(),
                offset,
                userDTO.getPageSize()
        );

        int total = sysUserMapper.count(
                userDTO.getNickname(),
                userDTO.getTargetJob(),
                userDTO.getStatus()
        );

        List<UserVO> voList = new ArrayList<>();
        for (SysUser user : userList) {
            voList.add(convertToVO(user));
        }

        return new PageResult<>((long) total, voList);
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void updateStatus(Long id, Integer status) {
        String lockKey = "user:status:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }
            sysUserMapper.updateStatus(id, status);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void updateLevel(Long id, Integer currentLevel) {
        String lockKey = "user:level:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }
            sysUserMapper.updateLevel(id, currentLevel);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void updateTargetJob(Long id, String targetJob) {
        String lockKey = "user:targetJob:" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("操作太频繁，请稍后重试");
            }
            
            SysUser existUser = sysUserMapper.selectById(id);
            if (existUser == null) {
                throw new BusinessException("用户不存在");
            }
            
            existUser.setTargetJob(targetJob);
            sysUserMapper.updateById(existUser);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public UserDetailVO getUserDetail(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }

        UserDetailVO detailVO = new UserDetailVO();
        detailVO.setUser(convertToVO(sysUser));
        detailVO.setFavorites(userFavoriteService.getUserFavorites(id));
        detailVO.setWrongs(userWrongService.getUserWrongDetails(id));
        detailVO.setClocks(userClockService.getUserClockDetails(id));

        return detailVO;
    }

    private UserVO convertToVO(SysUser sysUser) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(sysUser, userVO);
        return userVO;
    }
}

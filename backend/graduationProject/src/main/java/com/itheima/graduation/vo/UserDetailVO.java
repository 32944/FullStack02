package com.itheima.graduation.vo;

import java.util.List;

public class UserDetailVO {
    private UserVO user;
    private List<ProblemVO> favorites;
    private List<UserWrongVO> wrongs;
    private List<UserClockVO> clocks;

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public List<ProblemVO> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<ProblemVO> favorites) {
        this.favorites = favorites;
    }

    public List<UserWrongVO> getWrongs() {
        return wrongs;
    }

    public void setWrongs(List<UserWrongVO> wrongs) {
        this.wrongs = wrongs;
    }

    public List<UserClockVO> getClocks() {
        return clocks;
    }

    public void setClocks(List<UserClockVO> clocks) {
        this.clocks = clocks;
    }
}

package com.chenkewen.administrator.myhelper.model;

/**
 * Created by Administrator on 2016/3/21.
 */
public class Song {
    //歌曲的名称
    private String mSongName;
    //歌曲的文件名
    private String mSongFileName;
    //歌曲的名称的长度
    private int mNameLenght;

    public char[] getNameCharacters() {
        return mSongName.toCharArray();
    }

    public String getmSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        mSongName = songName;
        mNameLenght = songName.length();
    }

    public String getSongFileName() {
        return mSongFileName;
    }

    public void setSongFileName(String songFileName) {
        mSongFileName = songFileName;
    }

    public int getNameLenght() {
        return mNameLenght;
    }
}

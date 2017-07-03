package com.example.joju.myapplication7;

import java.io.Serializable;
/*
음악 데이터형식 지정 (음악ID, 이름, 가수, 이미지ID)
ArrayList에 자료형으로 사용
 */

public class DataClass implements Serializable  //Serializable : 변수를 직렬화 시킴 http://rockdrumy.tistory.com/1044
{
    String musicID;     //음악ID
    String name;        //이름 저장
    String artist;      //가수 이름
    String imgId;       //이미지 변수

    public DataClass() {
    }

    public DataClass(String musicID, String name, String artist, String imgId) {
        this.musicID = musicID;
        this.name = name;
        this.artist = artist;
        this.imgId = imgId;
    }

    public String getMusicID() {
        return musicID;
    }                //음악ID 반환

    public void setId(String musicID) {
        this.musicID = musicID;
    }  //음악ID 설정

    public String getImgId() {
        return imgId;
    }                    //이미지ID 반환

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }    //이미지ID 설정

    public String getname() {
        return this.name;
    }                 //이름 반환

    @Override
    public String toString() {
        return "DataClass{" +
                "id='" + musicID + '\'' +
                ", imgId='" + imgId + '\'' +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

    public void setTitle(String name) {
        this.name = name;
    }         //이름설정

    public String getArtist() {
        return artist;
    }                    //가수이름 반환

    public void setArtist(String artist) {
        this.artist = artist;
    }  //가수이름 설정

}

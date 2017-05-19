package com.example.achuan.teamsystem.model.bean;


import com.example.achuan.teamsystem.util.StringUtil;

import cn.bmob.v3.BmobObject;

/**
 * Created by achuan on 16-10-22.
 * 功能：1-创建BmobObject对象
 *     在Bmob中,BmobObject就相当于数据库中的一张表,每个属性就相当于表的字段
 *    每一个BmobObject对象就相当于表里的一行数据
 *    2-添加Comparable接口实现数据排序
 */
public class Course extends BmobObject implements Comparable{
    //Course课程表数据类型
    private String Cno;//课程号
    private String Cname;//课程名
    private Double Credit;//学分:0~6
    private Integer Semester;//学期：1~8

    public String getCno() {
        return Cno;
    }

    public void setCno(String cno) {
        Cno = cno;
    }

    public String getCname() {
        return Cname;
    }

    public void setCname(String cname) {
        Cname = cname;
    }

    public Double getCredit() {
        return Credit;
    }

    public void setCredit(Double credit) {
        Credit = credit;
    }

    public Integer getSemester() {
        return Semester;
    }

    public void setSemester(Integer semester) {
        Semester = semester;
    }

    //数据核对的规则
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Course that = (Course) obj;
        return getCname().equals(that.getCname())&&
                getCredit().equals(that.getCredit())&&
                getSemester().equals(this.getSemester());
    }

    //自定义比较规则,实现数据集合的排序
    @Override
    public int compareTo(Object o) {
        /**
         * compareTo()：大于0表示前一个数据比后一个数据大， 0表示相等，小于0表示前一个数据小于后一个数据
         * 相等时会走到equals()
         */
        if (o instanceof Course) {
            Course that = (Course) o;
            int num=1;//默认为小于前一个
            //StringUtil.getHeadChar(getCname()) == ' '代表属于"#"那一部分的内容
            if (StringUtil.getHeadChar(getCname()) == ' ') {
                num= 1;//小于
                if (StringUtil.getHeadChar(that.getCname()) == ' '){
                    num= 0;//等于
                }
            }else if(StringUtil.getHeadChar(that.getCname()) == ' '){
                num= -1;//大于
            } else if (StringUtil.getHeadChar(that.getCname()) > StringUtil.getHeadChar(getCname())) {
                num= -1;//大于
            } else if (StringUtil.getHeadChar(that.getCname()) == StringUtil.getHeadChar(getCname())) {
                num= 0;//等于
            }
            if(num==0){
                //如果字母相同，再比较学期
                num=this.Semester.compareTo(that.Semester);
                if(num==0){
                    //如果字母、学期都相同,比较学分
                    return this.Credit.compareTo(that.Credit);
                }else {
                    return num;
                }
            }else {
                return num;
            }
        } else {
            throw new ClassCastException();
        }
    }
}

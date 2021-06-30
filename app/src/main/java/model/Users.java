package model;

public class Users {
    String Uid;
    String fname;
    String lname;
    String email;
    String status;


    public Users() {
    }

    public Users(String uid, String fname, String lname, String email, String status) {
        Uid = uid;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.status = status;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}

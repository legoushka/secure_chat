import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String phonenum;
    private String name;
    private String surnname;
    private Boolean gender;
    private String iconURL;

    private String publicPGPkey;

    public User(int id, String phonenum, String name, String surnname, Boolean gender, String iconURL, String publicPGPkey) {
        this.id = id;
        this.phonenum = phonenum;
        this.name = name;
        this.surnname = surnname;
        this.gender = gender;
        this.iconURL = iconURL;
        this.publicPGPkey = publicPGPkey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhonenum() {return phonenum;}

    public String getName() {
        return name;
    }
    public String getSurnname() {return surnname;}

    public Boolean getGender() {
        return this.gender;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getPublicPGPkey() {
        return publicPGPkey;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + phonenum.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;

        if (this.id == user.id) {
            return true;
        }
        return false;
    }
}
public class User {
    private int id;
    private String Username;
    private String Password;
    private String First_Name;
    private String Last_Name;
    private String Role;

    public User(int id, String Username, String Password, String First_Name, String Last_Name, String Role){
        this.id = id;
        this.Username = Username;
        this.Password = Password;
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.Role = Role;

    }


    public int getId(){return id;}
    public void setID(){};

    public String getUsername() {return Username;}
    public void setUsername(String username) {Username = username;}


    public String getPassword() {return Password;}
    public void setPassword(String password) {Password = password;}



    public String getFirst_Name() {return First_Name;}
    public void setFirst_Name(String first_Name) {First_Name = first_Name;}


    public String getLast_Name() {return Last_Name;}
    public void setLast_Name(String last_Name) {Last_Name = last_Name;}



    public String getRole() {return Role;}
    public void setRole(String role) {Role = role;}
}

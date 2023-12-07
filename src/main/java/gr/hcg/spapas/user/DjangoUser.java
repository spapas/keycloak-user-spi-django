package gr.hcg.spapas.user;


import java.util.List;

/**
 * @author Serafeim Papastefanos
 */
public class DjangoUser {

    public String id;
    public String username;
    public String email;
    public String firstName;
    public String lastName;
    public Long created;
    public List<String> roles = null;
    public String password;
    public String fatherName;
    public String motherName;
    public String dob;

    public String toString() {
        return email+" " + firstName + " " + lastName;
    }

}

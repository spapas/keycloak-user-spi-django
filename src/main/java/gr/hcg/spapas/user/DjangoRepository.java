package gr.hcg.spapas.user;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Serafeim Papastefanos
 */
class DjangoRepository {
    DataSource ds;
    DjangoRepository(DataSource ds) {
        Long created = System.currentTimeMillis();
        this.ds = ds;

    }

    String USER_SQL = "select pn.email as email, pn.password as password, " +
            "pn.first_name as first_name, pn.last_name as last_name, " +
            "pp.father_name, pp.mother_name, pp.dob " +
            "from profiles_nousernameuser pn " +
            "LEFT JOIN account_emailaddress ae on ae.email=pn.email " +
            "LEFT JOIN profiles_profile pp on pp.user_id=pn.id " +
            "where ae.verified=1";


    int getUsersCount() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ds.getConnection();
            stmt = con.prepareStatement("SELECT count(*) from profiles_nousernameuser");
            rs = stmt.executeQuery();
            int len = 0;
            while (rs.next()) {
                len = rs.getInt(1);
            }
            return len;

        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (con!=null) {
                try {
                    con.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    DjangoUser findUserById(String id) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ds.getConnection();

            stmt = con.prepareStatement(USER_SQL+" and pn.email = ?");
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            System.out.println(con);
            System.out.println(stmt);
            System.out.println(rs);
            int len = 0;
            DjangoUser user = new DjangoUser();
            while (rs.next()) {
                user.email = rs.getString("email");
                user.username = user.email;
                user.firstName = rs.getString("first_name");
                user.lastName = rs.getString("last_name");
                user.password = rs.getString("password");
                user.fatherName = rs.getString("father_name");
                user.motherName = rs.getString("mother_name");
                user.dob = rs.getString("dob");
            }
            return user;

        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (con!=null) {
                try {
                    con.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    DjangoUser findUserByUsernameOrEmail(String username) {
        return findUserById(username);
    }

    List<DjangoUser> findUsers(String query) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String likeQuery = "%"+query+"%";
            con = ds.getConnection();

            stmt = con.prepareStatement(USER_SQL + " and pn.email like ?");
            stmt.setString(1, likeQuery);
            rs = stmt.executeQuery();
            System.out.println(con);
            System.out.println(stmt);
            System.out.println(rs);
            ArrayList<DjangoUser> users = new ArrayList<DjangoUser>();
            while (rs.next()) {
                DjangoUser user = new DjangoUser();
                user.email = rs.getString("email");
                user.username = user.email;
                user.firstName = rs.getString("first_name");
                user.lastName = rs.getString("last_name");
                user.password = rs.getString("password");
                user.fatherName = rs.getString("father_name");
                user.motherName = rs.getString("mother_name");
                user.dob = rs.getString("dob");
                users.add(user);
            }
            return users;

        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (con!=null) {
                try {
                    con.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<DjangoUser>();
    }



}

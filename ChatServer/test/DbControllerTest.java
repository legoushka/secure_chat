import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class DbControllerTest {

    @Test
    void openInvalidConnection() {
        DbController db = new DbController("invalid", "invalid", "invalid");
        assertThrows(SQLException.class,
                () -> {
                    db.openConnection();
                });
    }

    @Test
    void openConnection() {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        assertDoesNotThrow(
                () -> {
                    db.openConnection();
                }
        );
    }

    @Test
    void closeConnection() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        assertDoesNotThrow(
                () -> {
                    db.closeConnection();
                }
        );
    }

    @Test
    void closeNotOpenedConnection() {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        assertThrows(NullPointerException.class,
                () -> {
                    db.closeConnection();
                }
        );
    }

    @Test
    void compareUserPasswords() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        assertEquals(
                true,
                db.compareUserPasswords("77777777777", "password")
        );
    }

    @Test
    void compareInvalidUserPassword() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        assertEquals(
                false,
                db.compareUserPasswords("77777777777", "invalid")
        );
    }

    @Test
    void compareInvalidUserPhonenum() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        assertEquals(
                false,
                db.compareUserPasswords("0", "password")
        );
    }

    @Test
    void compareUserPasswordWithoutConnection(){
        DbController db = new DbController("invalid", "invalid", "invalid");
        assertThrows(NullPointerException.class,
                ()->
                {
                    db.compareUserPasswords("77777777777", "password");
                });
    }

    @Test
    void isRegLoginNotFree() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        assertEquals(
                false,
                db.isRegLoginFree("77777777777")
        );
    }

    @Test
    void isRegLoginFree() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        assertEquals(
                true,
                db.isRegLoginFree("free")
        );
    }

    @Test
    void getActualUserData() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        User user = db.getActualUserData(new User(0, "77777777777", null, null, null, null, null));
        assertTrue(user.getId() == 1 && Objects.equals(user.getPhonenum(), "77777777777") && Objects.equals(user.getName(), "unit")
                && Objects.equals(user.getSurnname(), "test") && user.getGender() == true && Objects.equals(user.getIconURL(), "unittest"));
    }

    @Test
    void getActualUserDataNull() throws SQLException {
        DbController db = new DbController("postgres", "root", "jdbc:postgresql://localhost:5432/postgres");
        db.openConnection();
        User user = db.getActualUserData(new User(0, "null", null,null,null,null,null));
        assertNull(user);
    }

    @Test
    void registerNewUser() {

    }
}
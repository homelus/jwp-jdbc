package core.jdbc;

import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultSetTest {

    private SimpleResultSet resultSet;
    private ResultSetRow<Client> rsRow;
    private ResultSetSupport rsSupport;
    private Client expectedClient1 = new Client("jun", "0000", "hyunjun", 30);
    private Client expectedClient2 = new Client("min", "1111", "hyunmin", 29);

    private final RowMapper<Client> rsMapper = rs -> new Client(
            rs.getString("id"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getInt("age"));

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        resultSet = new SimpleResultSet();
        resultSet.addColumn("id", Types.VARCHAR, 1, 1);
        resultSet.addColumn("password", Types.VARCHAR, 2, 1);
        resultSet.addColumn("name", Types.VARCHAR, 1, 1);
        resultSet.addColumn("age", Types.NUMERIC, 1, 1);
        resultSet.addRow(expectedClient1.getId(), expectedClient1.getPassword(), expectedClient1.getName(), expectedClient1.getAge());
        resultSet.addRow(expectedClient2.getId(), expectedClient2.getPassword(), expectedClient2.getName(), expectedClient2.getAge());

        rsRow = new ResultSetRow(Client.class);
        rsSupport = new ResultSetSupport(resultSet);
    }

    @Test
    void resultSetRow() throws SQLException {
        resultSet.next();
        Client client1 = new Client();
        rsRow.populateRow(client1, resultSet);
        assertClient(client1, expectedClient1, false);

        resultSet.next();
        Client client2 = new Client();
        rsRow.populateRow(client2, resultSet);
        assertClient(client2, expectedClient2, false);
    }

    @Test
    void resultSetSupportByReflection() throws SQLException {
        final List<Client> clients = rsSupport.getResults(Client.class);
        assertThat(clients.size()).isEqualTo(2);
        assertClient(clients.get(0), expectedClient1, false);
        assertClient(clients.get(1), expectedClient2, false);

    }

    @Test
    void resultSetSupportByMapper() throws SQLException {
        final List<Client> clients = rsSupport.getResults(rsMapper);
        assertThat(clients.size()).isEqualTo(2);
        assertClient(clients.get(0), expectedClient1, true);
        assertClient(clients.get(1), expectedClient2, true);
    }

    private void assertClient(Client client, Client expected, boolean isCheckAge) {
        assertThat(client.getId()).isEqualTo(expected.getId());
        assertThat(client.getPassword()).isEqualTo(expected.getPassword());
        assertThat(client.getName()).isEqualTo(expected.getName());

        if (isCheckAge) {
            assertThat(client.getAge()).isEqualTo(expected.getAge());
        } else {
            assertThat(client.getAge()).isEqualTo(0);
        }
    }

    @Test
    void resultSetIterate() throws SQLException {
        resultSet.next();
        final String id = resultSet.getString("id");
        final String password = resultSet.getString("password");
        final String name = resultSet.getString("name");
        assertThat(id).isEqualTo("jun");
        assertThat(password).isEqualTo("0000");
        assertThat(name).isEqualTo("hyunjun");

        resultSet.next();
        final String id2 = resultSet.getString("id");
        final String password2 = resultSet.getString("password");
        final String name2 = resultSet.getString("name");
        assertThat(id2).isEqualTo("min");
        assertThat(password2).isEqualTo("1111");
        assertThat(name2).isEqualTo("hyunmin");
    }

    private static class Client {
        private String id;
        private String password;
        private String name;
        private int age;

        public Client() {
        }

        public Client(String id, String password, String name, int age) {
            this.id = id;
            this.password = password;
            this.name = name;
            this.age = age;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

    }

}

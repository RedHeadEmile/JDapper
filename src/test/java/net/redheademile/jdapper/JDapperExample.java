package net.redheademile.jdapper;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JDapperExample {
    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setDatabaseName("jdappertest");
        dataSource.setUser("root");
        dataSource.setPassword("");
        dataSource.setServerName("localhost");
        dataSource.setCharacterEncoding("utf-8");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Player> players = new ArrayList<>();

        RowMapper<Player> playerMapper = JDapper.getMapper(
                Player.class, PlayerMessage.class,
                (player, playerMessage) -> {
                    Player listedPlayer = players.stream().filter(p -> p.uuid.equals(player.uuid)).findAny().orElse(null);
                    if (listedPlayer == null) {
                        listedPlayer = player;
                        players.add(player);
                    }

                    listedPlayer.messages.add(playerMessage);
                    return listedPlayer;
                },
                "uuid", "PlayerMessageId"
        );

        jdbcTemplate.query("SELECT player.*, playermessage.* FROM player LEFT JOIN playermessage ON player.uuid = playermessage.Player_PlayerId", playerMapper);

        System.out.println(players);
    }

    public static class Player {
        private String uuid;
        private boolean allowMessages;
        private long firstConnection;
        private String username;
        private Short preferedColor;

        private List<PlayerMessage> messages = new ArrayList<>();

        @Override
        public String toString() {
            return "Player{" +
                    "uuid='" + uuid + '\'' +
                    ", allowMessages=" + allowMessages +
                    ", firstConnection=" + firstConnection +
                    ", username='" + username + '\'' +
                    ", preferedColor=" + preferedColor +
                    ", messages=" + messages +
                    '}';
        }
    }

    public static class PlayerMessage {
        private long PlayerMessageId;
        private String Player_PlayerId;
        private String Message;
        private byte[] AdditionalContent;

        @Override
        public String toString() {
            return "PlayerMessage{" +
                    "PlayerMessageId=" + PlayerMessageId +
                    ", Player_PlayerId='" + Player_PlayerId + '\'' +
                    ", Message='" + Message + '\'' +
                    ", AdditionalContent=" + Arrays.toString(AdditionalContent) +
                    '}';
        }
    }
}

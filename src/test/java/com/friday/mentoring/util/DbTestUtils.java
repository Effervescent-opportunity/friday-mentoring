//package com.friday.mentoring.util;
//
//import com.friday.mentoring.db.entity.AuthEventEntity;
//import com.friday.mentoring.db.entity.OutboxEntity;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.List;
//
//public class DbTestUtils {
//
//    public static Integer getAuthEventCount(JdbcTemplate jdbcTemplate) {
//        return jdbcTemplate.queryForObject("select count(*) from auth_event", Integer.class);
//    }
//
//    public static List<AuthEventEntity> getAuthEvents(JdbcTemplate jdbcTemplate) {
//        return jdbcTemplate.queryForList("select * from auth_event", (rs, r));
//    }
//
//    public static Integer getOutboxCount(JdbcTemplate jdbcTemplate) {
//        return jdbcTemplate.queryForObject("select count(*) from outbox", Integer.class);
//    }
//
//    public static List<OutboxEntity> getOutboxEntities(JdbcTemplate jdbcTemplate) {
//        return jdbcTemplate.queryForList("select * from outbox", OutboxEntity.class);
//    }
//}

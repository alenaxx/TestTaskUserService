package com.example.demo.dao;

import com.example.demo.dto.DateStatusDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserStatusDto;
import com.example.demo.exception.EmailNotValidException;
import com.example.demo.exception.PhoneNumberNotValidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Status;
import com.example.demo.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository("postgres")
public class UserDaoService implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final String addUserSql = "INSERT INTO userr ( id,name,email,phoneNumber,status) VALUES (?, ?, ?, ?, ?)";
    private final String getUserSql = " SELECT id,name,email,phoneNumber,status FROM userr WHERE id = ? ";
    private final String getStatusTime = "SELECT id, dateTime,status FROM userr WHERE status = ? ";
    private final String changeStatusSql = "UPDATE userr SET status = ?, dateTime = ? WHERE id = ?";
    private final RowMapper<UserDto> userRowMapper = (resultSet, i) -> {
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String phoneNumber = resultSet.getString("phoneNumber");
        String status = resultSet.getString("status");
        return new UserDto(name, email, phoneNumber, status);
    };
    private final RowMapper<DateStatusDto> dateStatusRowMapper = (resultSet, i) -> {
        UUID id1 = UUID.fromString(resultSet.getString("id"));
        Long dateTime = resultSet.getLong("dateTime");
        String status = resultSet.getString("status");
        return new DateStatusDto(dateTime, status, id1);
    };

    @Autowired
    public UserDaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private boolean isPhoneNumberValid(String number) {
        Pattern p = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");
        Matcher m = p.matcher(number);
        return (m.find() && m.group().equals(number));
    }

    @Override
    public UUID addUser(UUID id, User user) throws EmailNotValidException, PhoneNumberNotValidException {
        if (isEmailValid(user.getEmail())) {
            if (isPhoneNumberValid(user.getPhoneNumber())) {
                jdbcTemplate.update(addUserSql,
                        id, user.getName(), user.getEmail(), user.getPhoneNumber(), Status.Offline.toString());
                return id;
            } else
                throw new PhoneNumberNotValidException("Phone number should be like +7xxxxxxxx");
        } else
            throw new EmailNotValidException("Email should be like xxxxx@xxxx.ru");
    }

    @Override
    public UserDto getUser(UUID id) throws UserNotFoundException {
        try {
            return jdbcTemplate.queryForObject(getUserSql, new Object[]{id},
                    userRowMapper);
        } catch (DataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public UserStatusDto changeStatus(UUID id, String status) throws UserNotFoundException {
        UserDto user = getUser(id);
        switch (status) {
            case ("Online"):
                jdbcTemplate.update(changeStatusSql, Status.Online.toString(), new Date().getTime(), id
                );
                break;
            case ("Away"):
                jdbcTemplate.update(changeStatusSql, Status.Away.toString(), new Date().getTime(), id
                );
                break;
            case ("Offline"):
                jdbcTemplate.update(changeStatusSql, Status.Offline.toString(), new Date().getTime(), id
                );
                break;
            default:
                throw new DataIntegrityViolationException("Status not valid");
        }
        return new UserStatusDto(id, user.getStatus(), status);

    }

    private void updateStatus(UUID id) {
        jdbcTemplate.update(changeStatusSql, Status.Away.toString(), new Date().getTime(), id
        );
    }

    @Scheduled(fixedRate = 10000)
    public void changeStatus() throws NullPointerException, EmptyResultDataAccessException {
        try {
            List<DateStatusDto> dateStatusDtos = new ArrayList<>();
            DateStatusDto dateStatusDto = jdbcTemplate.queryForObject(getStatusTime, new Object[]{"Online"}, dateStatusRowMapper);
            dateStatusDtos.add(dateStatusDto);
            for (DateStatusDto statusDto : dateStatusDtos) {
                Date date = new Date();
                if (date.getTime() - statusDto.getDate() >= 300000) {
                    assert dateStatusDto != null;
                    updateStatus(dateStatusDto.id);
                }
            }
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            return;
        }
    }

}


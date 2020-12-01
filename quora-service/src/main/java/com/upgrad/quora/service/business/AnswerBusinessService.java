package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AnswerBusinessService {


    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;

    public QuestionEntity getQuestionByUUId(final String uuid) throws InvalidQuestionException {
        QuestionEntity question = questionDao.getQuestionByuuid(uuid);
        if (question == null)
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        else
            return question;
    }


    public UserAuthTokenEntity getUserAuthTokenEntity(String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
                .isAfter(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to post an answer");
        }
        return userAuthTokenEntity;
    }

    public AnswerEntity createAnswer(final AnswerEntity answer){
        answerDao.createAnswer(answer);
        return answer;
    }

    public AnswerEntity getAnswerByUUId(final String uuid) throws AnswerNotFoundException {
        AnswerEntity answer = answerDao.getAnswerByUuid(uuid);
        if (answer == null)
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        else
            return answer;
    }

    public AnswerEntity editAnswer(final AnswerEntity answerEntity,final String userUuid,final String content) throws AuthorizationFailedException {
        if(answerEntity.getUser().getUuid()!=userUuid)
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        answerEntity.setAns(content);
        answerDao.updateAnswer(answerEntity);
        return answerEntity;
    }
}
package org.transaction.test.global_transaction.mybatis.service.datasource2.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User2;
import org.transaction.test.global_transaction.mybatis.mapper2.DataSource2User2Mapper;
import org.transaction.test.global_transaction.mybatis.service.datasource2.User2Service;

@Service("user2ServiceDataSource2")
public class User2ServiceImpl implements User2Service {

    @Autowired
    private DataSource2User2Mapper user2Mapper;

    public void truncate() {
        user2Mapper.truncated();
    }

    @Override
    public void add(DataSource2User2 user2){
        user2Mapper.insert(user2);
    }

    @Override
    public void addException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequired(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequiredException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupports(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupportsException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNew(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNewException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupported(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupportedException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatory(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatoryException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNever(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNeverException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNested(DataSource2User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNestedException(DataSource2User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }
}

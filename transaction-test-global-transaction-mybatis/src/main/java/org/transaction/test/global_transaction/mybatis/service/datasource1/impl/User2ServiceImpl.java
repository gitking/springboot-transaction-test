package org.transaction.test.global_transaction.mybatis.service.datasource1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User2;
import org.transaction.test.global_transaction.mybatis.mapper1.DataSource1User2Mapper;
import org.transaction.test.global_transaction.mybatis.service.datasource1.User2Service;

@Service("user2ServiceDataSource1")
public class User2ServiceImpl implements User2Service {

    @Autowired
    private DataSource1User2Mapper user2Mapper;

    public void truncate() {
        user2Mapper.truncated();
    }

    @Override
    public void add(DataSource1User2 user2){
        user2Mapper.insert(user2);
    }

    @Override
    public void addException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequired(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequiredException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupports(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupportsException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNew(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNewException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupported(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupportedException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatory(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatoryException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNever(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNeverException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNested(DataSource1User2 user2) {
        user2Mapper.insert(user2);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNestedException(DataSource1User2 user2) {
        user2Mapper.insert(user2);
        throw new RuntimeException();
    }
}

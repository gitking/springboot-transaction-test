package org.transaction.test.global_transaction.mybatis.service.datasource1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.transaction.test.global_transaction.mybatis.bean.datasource1.DataSource1User1;
import org.transaction.test.global_transaction.mybatis.mapper1.DataSource1User1Mapper;
import org.transaction.test.global_transaction.mybatis.service.datasource1.User1Service;

@Service("user1ServiceDataSource1")
public class User1ServiceImpl implements User1Service {

    @Autowired
    private DataSource1User1Mapper user1Mapper;

    @Override
    public void truncate(){
        user1Mapper.truncated();
    }

    @Override
    public void add(DataSource1User1 user1){
        user1Mapper.insert(user1);

        // 在没有主动开启事务的情况下，事务时自动提交的。下面的代码报错，不影响上面的插入结果，上面依然会插入成功
//        User1 user11 = new User1();
//        user11.setName("我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我");
//        user1Mapper.insert(user11);
    }

    @Override
    public void addException(DataSource1User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequired(DataSource1User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequiredException(DataSource1User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupports(DataSource1User1 user1){
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupportsException(DataSource1User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNew(DataSource1User1 user1){
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNewException(DataSource1User1 user1){
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupported(DataSource1User1 user1){
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupportedException(DataSource1User1 user1){
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatory(DataSource1User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatoryException(DataSource1User1 user1){
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNever(DataSource1User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNeverException(DataSource1User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNested(DataSource1User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNestedException(DataSource1User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }
}

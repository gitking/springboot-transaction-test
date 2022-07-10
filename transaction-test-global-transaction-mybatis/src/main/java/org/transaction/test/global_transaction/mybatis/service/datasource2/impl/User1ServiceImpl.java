package org.transaction.test.global_transaction.mybatis.service.datasource2.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.transaction.test.global_transaction.mybatis.bean.datasource2.DataSource2User1;
import org.transaction.test.global_transaction.mybatis.mapper2.DataSource2User1Mapper;
import org.transaction.test.global_transaction.mybatis.service.datasource2.User1Service;

@Service("user1ServiceDataSource2")
public class User1ServiceImpl implements User1Service {

    @Autowired
    private DataSource2User1Mapper user1Mapper;

    @Override
    public void truncate(){
        user1Mapper.truncated();
    }

    @Override
    public void add(DataSource2User1 user1){
        user1Mapper.insert(user1);

        // 在没有主动开启事务的情况下，事务时自动提交的。下面的代码报错，不影响上面的插入结果，上面依然会插入成功
//        User1 user11 = new User1();
//        user11.setName("我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我我");
//        user1Mapper.insert(user11);
    }

    @Override
    public void addException(DataSource2User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequired(DataSource2User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRequiredException(DataSource2User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupports(DataSource2User1 user1){
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void addSupportsException(DataSource2User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNew(DataSource2User1 user1){
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRequiresNewException(DataSource2User1 user1){
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupported(DataSource2User1 user1){
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupportedException(DataSource2User1 user1){
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatory(DataSource2User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addMandatoryException(DataSource2User1 user1){
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNever(DataSource2User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void addNeverException(DataSource2User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNested(DataSource2User1 user1) {
        user1Mapper.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void addNestedException(DataSource2User1 user1) {
        user1Mapper.insert(user1);
        throw new RuntimeException();
    }
}

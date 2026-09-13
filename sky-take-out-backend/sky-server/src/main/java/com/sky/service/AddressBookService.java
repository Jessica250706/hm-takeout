package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    List<AddressBook> list();

    /**
     * 查询默认地址
     *
     * @return
     */
    AddressBook getDefault();

    /**
     * 根据 id 查询地址
     *
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    void addAddressBook(AddressBook addressBook);

    /**
     * 根据 id 修改地址
     *
     * @param addressBook
     * @return
     */
    void updateAddressBook(AddressBook addressBook);

    /**
     * 设置默认地址
     *
     * @param id
     * @return
     */
    void setDefaultAddress(Long id);

    /**
     * 根据 id 删除地址
     *
     * @param id
     * @return
     */
    void deleteById(Long id);
}

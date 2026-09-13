package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @Override
    public List<AddressBook> list() {
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return addressBookMapper.list(addressBook);
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @Override
    public AddressBook getDefault() {
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(StatusConstant.ENABLE)
                .build();
        List<AddressBook> addressBooks = addressBookMapper.list(addressBook);
        if (addressBooks != null && !addressBooks.isEmpty()) {
            return addressBooks.get(0);
        }
        return null;
    }

    /**
     * 根据 id 查询地址
     *
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = AddressBook.builder()
                .id(id)
                .build();
        List<AddressBook> addressBooks = addressBookMapper.list(addressBook);
        if (addressBooks != null && !addressBooks.isEmpty()) {
            return addressBooks.get(0);
        }
        return null;
    }

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @Override
    public void addAddressBook(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(StatusConstant.DISABLE);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据 id 修改地址
     *
     * @param addressBook
     * @return
     */
    @Override
    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }


    /**
     * 设置默认地址
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public void setDefaultAddress(Long id) {
        // 1.将当前用户的所有地址修改为非默认地址
        AddressBook addressBook = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(StatusConstant.DISABLE)
                .build();
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        // 2.将当前地址改为默认地址
        addressBook.setId(id);
        addressBook .setIsDefault(StatusConstant.ENABLE);
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据 id 删除地址
     *
     * @param id
     * @return
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}

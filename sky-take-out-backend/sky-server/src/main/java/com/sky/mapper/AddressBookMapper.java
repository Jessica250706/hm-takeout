package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 条件查询查询地址信息
     *
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    void insert(AddressBook addressBook);

    /**
     * 根据 id 修改地址
     *
     * @param addressBook
     * @return
     */
    void update(AddressBook addressBook);

    /**
     * 设置当前用户的所有地址为非默认地址
     *
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据 id 删除地址
     *
     * @param id
     * @return
     */
    @Delete("delete from address_book where id = #{id};")
    void deleteById(Long id);
}

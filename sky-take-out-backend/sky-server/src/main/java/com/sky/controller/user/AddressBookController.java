package com.sky.controller.user;

import com.sky.dto.AddressBookDefaultDTO;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("UserAddressBookController")
@RequestMapping("/user/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的所有地址信息
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        List<AddressBook> addressBookList = addressBookService.list();
        return Result.success(addressBookList);
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = addressBookService.getDefault();
        return Result.success(addressBook);
    }

    /**
     * 根据 id 查询地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        addressBookService.addAddressBook(addressBook);
        return Result.success();
    }

    /**
     * 根据 id 修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result updateAddressBook(@RequestBody AddressBook addressBook) {
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     *
     * @param addressBookDefaultDTO
     * @return
     */
    @PutMapping("/default")
    public Result setDefaultAddress(@RequestBody AddressBookDefaultDTO addressBookDefaultDTO) {
        addressBookService.setDefaultAddress(addressBookDefaultDTO.getId());
        return Result.success();
    }

    /**
     * 根据 id 删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result delete(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }
}

package com.nevertrouble.demo.mybatisone.dao;

import com.nevertrouble.demo.mybatisone.bean.Blog;
import org.apache.ibatis.annotations.Param;

public interface BlogMapper {

    Blog addBlog(Blog blog);

    Blog selectBlog(@Param("id") int id);
}

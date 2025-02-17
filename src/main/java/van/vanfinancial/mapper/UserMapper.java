package van.vanfinancial.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import van.vanfinancial.entity.User;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(@Param("userName") String username);
}

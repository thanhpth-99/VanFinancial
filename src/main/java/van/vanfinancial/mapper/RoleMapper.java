package van.vanfinancial.mapper;

import org.apache.ibatis.annotations.Mapper;
import van.vanfinancial.entity.Role;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> getAll();
}

package van.vanfinancial.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import van.vanfinancial.entity.InvalidatedToken;

@Mapper
public interface InvalidatedTokenMapper {
    void insertValidatedToken(@Param("id") String id, @Param("refreshToken") String refreshToken);
    void deleteValidatedToken(@Param("id") String id);
    boolean isValidatedTokenExists(@Param("jwtId") String jwtId, @Param("refreshToken") String refreshToken);
}

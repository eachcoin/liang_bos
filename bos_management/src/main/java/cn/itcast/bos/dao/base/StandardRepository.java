package cn.itcast.bos.dao.base;

import cn.itcast.bos.domain.base.Standard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StandardRepository extends JpaRepository<Standard, Integer> {}

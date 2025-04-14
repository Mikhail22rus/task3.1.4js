package ru.tolstykh.crudspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.tolstykh.crudspringboot.model.User;

public interface UserRepository extends JpaRepository<User,Long>{
}

package Projekt.TanuloOsveny.repository;

import Projekt.TanuloOsveny.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByEducationLevel(User.EducationLevel educationLevel);

    List<User> findByAgeOrderByFullNameAsc(int age);
}
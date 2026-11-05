package id.pasarkita.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p "
            + "where lower(p.name) like lower(concat('%', :q, '%')) "
            + "or lower(p.sku) like lower(concat('%', :q, '%'))")
    Page<Product> search(@Param("q") String q, Pageable pageable);

    List<Product> findTop60BySellerIdOrderByIdAsc(Long sellerId);
}

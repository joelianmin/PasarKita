package id.pasarkita.catalog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
class SellerProfileController {

    private final SellerRepository sellers;
    private final ProductRepository products;
    private final FeatureFlags flags;
    private final ThumbnailService thumbnails;

    SellerProfileController(SellerRepository sellers, ProductRepository products,
                            FeatureFlags flags, ThumbnailService thumbnails) {
        this.sellers = sellers;
        this.products = products;
        this.flags = flags;
        this.thumbnails = thumbnails;
    }

    @GetMapping("/api/sellers/{id}/profile")
    SellerProfile profile(@PathVariable Long id) {
        Seller seller = sellers.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        boolean dynamicThumbnail = flags.isEnabled("seller.dynamic-thumbnail");
        List<ProductCard> cards = products.findTop60BySellerIdOrderByIdAsc(id).stream()
                .map(p -> new ProductCard(p.getId(), p.getName(), p.getPrice(),
                        dynamicThumbnail ? thumbnails.thumbnailFor(p.getId()) : "/images/" + p.getId() + ".jpg"))
                .toList();
        return new SellerProfile(seller.getId(), seller.getName(), seller.getCity(), cards);
    }

    public record SellerProfile(Long id, String name, String city, List<ProductCard> products) {
    }

    public record ProductCard(Long id, String name, BigDecimal price, String thumbnail) {
    }
}

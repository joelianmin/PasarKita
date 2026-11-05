package id.pasarkita.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final StockRepository stocks;

    InventoryService(StockRepository stocks) {
        this.stocks = stocks;
    }

    @Transactional
    public void decrease(Long productId, int quantity) {
        Stock stock = stocks.findById(productId).orElseThrow(() -> new OutOfStockException(productId));
        if (stock.getQuantity() < quantity) {
            throw new OutOfStockException(productId);
        }
        stock.setQuantity(stock.getQuantity() - quantity);
    }
}

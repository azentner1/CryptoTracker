import com.andrej.cryptotracker.data.model.CryptoTicker
import org.junit.Assert.assertEquals
import org.junit.Test

class CryptoTickerTest {

    @Test
    fun `formattedSymbol should remove 't' and 'USD' from symbol`() {
        val ticker = CryptoTicker("tBTCUSD", 45000.0, 100.0, 0.05, 50000.0)
        assertEquals("BTC", ticker.formattedSymbol)
    }

    @Test
    fun `formattedSymbol should remove 'USD' and 'two little dots' from symbol`() {
        val ticker = CryptoTicker("BTC:USD", 45000.0, 100.0, 0.05, 50000.0)
        assertEquals("BTC", ticker.formattedSymbol)
    }

    @Test
    fun `formattedSymbol should return unchanged symbol if no 't' or 'USD'`() {
        val ticker = CryptoTicker("ETH", 45000.0, 100.0, 0.05, 50000.0)
        assertEquals("ETH", ticker.formattedSymbol)
    }

    @Test
    fun `formattedPrice should format price with dollar sign and commas`() {
        val ticker = CryptoTicker("BTC", 45000.123, 100.0, 0.05, 50000.0)
        assertEquals("$45,000.12", ticker.formattedPrice)
    }

    @Test
    fun `formattedPrice should format price correctly for large numbers`() {
        val ticker = CryptoTicker("BTC", 123456789.987, 100.0, 0.05, 50000.0)
        assertEquals("$123,456,789.99", ticker.formattedPrice)
    }

    @Test
    fun `formattedDailyChange should return correct percentage format`() {
        val ticker = CryptoTicker("BTC", 45000.0, 100.0, 0.05, 50000.0)
        assertEquals("5.00%", ticker.formattedDailyChange)
    }

    @Test
    fun `formattedDailyChange should handle negative percentages`() {
        val ticker = CryptoTicker("BTC", 45000.0, -100.0, -0.025, 50000.0)
        assertEquals("-2.50%", ticker.formattedDailyChange)
    }

    @Test
    fun `formattedDailyChange should handle percentages with decimals`() {
        val ticker = CryptoTicker("BTC", 45000.0, 100.0, 0.04567, 50000.0)
        assertEquals("4.57%", ticker.formattedDailyChange)
    }
}

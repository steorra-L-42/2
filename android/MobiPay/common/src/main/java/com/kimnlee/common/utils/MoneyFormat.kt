import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

fun moneyFormat(amount: BigInteger): String {
    val numberFormat = NumberFormat.getInstance(Locale.KOREA)
    return numberFormat.format(amount) + "원"
}
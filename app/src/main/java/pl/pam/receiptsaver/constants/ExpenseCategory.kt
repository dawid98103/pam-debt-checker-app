package pl.pam.receiptsaver.constants

enum class ExpenseCategory(val categoryName: String, val categoryId: String) {
    FOOD("Jedzenie", "0"),
    SERVICES("Usługi", "1"),
    CAR("Samochód", "2");

    companion object {
        fun getNameById(categoryId: String): String {
            return values().find { it.categoryId == categoryId }?.categoryName.orEmpty()
        }
    }
}
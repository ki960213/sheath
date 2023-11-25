package com.ki960213.sampleApp.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ki960213.sampleApp.databinding.ItemCartProductBinding
import com.ki960213.sampleApp.model.CartProduct

class CartProductViewHolder(
    private val binding: ItemCartProductBinding,
    private val dateFormatter: DateFormatter,
    onClickDelete: (cartProductId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivCartProductDelete.setOnClickListener {
            onClickDelete(
                binding.item?.id
                    ?: throw IllegalArgumentException("장바구니 아이템 삭제 버튼이 클릭될 때는 바인드가 되어야 합니다. CartProductViewHolder를 바인딩하는 로직을 다시 살펴보세요."),
            )
        }
    }

    fun bind(cartProduct: CartProduct) {
        binding.item = cartProduct
        binding.tvCartProductCreatedAt.text = dateFormatter.formatDate(cartProduct.createdAt)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            dateFormatter: DateFormatter,
            onClickDelete: (cartProductId: Long) -> Unit,
        ): CartProductViewHolder {
            val binding = ItemCartProductBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return CartProductViewHolder(binding, dateFormatter, onClickDelete)
        }
    }
}

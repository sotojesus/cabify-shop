package com.jesussoto.android.cabifyshop.data.api

import com.google.gson.Gson
import io.reactivex.rxjava3.core.Single

internal interface PromotionsDataSource {

    fun getPromotions(): Single<GetPromotionsResponse>
}

/**
 * FAKE data source to simulate a network-backed data source to fetch promotions information.
 */
internal class FakePromotionsDataSource(
    private val gson: Gson
): PromotionsDataSource {

    override fun getPromotions(): Single<GetPromotionsResponse> {
        return try {
            val fakeResponse = gson.fromJson(FAKE_PROMOTIONS_RESPONSE_JSON, GetPromotionsResponse::class.java)
            Single.just(fakeResponse)
        } catch (th: Throwable) {
            Single.error(th)
        }
    }

    companion object {

        // Fake server response to simulate fetching available promotions and configuration
        // from a remote data source.
        //
        // Ideally, the promotions information should COME ALONG the product information to better
        // maintain consistency and avoid multiple calls to server. For the scope of this challenge,
        // given the inability to modify the Products.json, I'm opting to fake another API operation
        // from which the promotions information are fetched.
        //
        // Modify this fake response to change the promotions available.
        // Some assumptions that should e kept in place for this to work are the following:
        // - A given product can have AT MOST 1 promotion available.
        // - The "type" field should be in sync with [PromotionType], of the app will be unable
        //   to parse and apply the promotion.
        //
        // The current fake response represents two promotions:
        // (1) 2-for-1 (Buy 1, Get 1) promotion for "VOUCHER".
        // (2) Bulk Reduced Unit Price to $19.0 on "TSHIRT" when at least of 3 items are added to cart.
        private val FAKE_PROMOTIONS_RESPONSE_JSON = """
            {
                "promotions": [
                    {
                        "productCode" : "VOUCHER",
                        "type" : "BuyXGetXPromotion",
                        "config": {
                            "minApplicableQuantity": 2,
                            "quantityToGetForFree": 1
                        }
                    },
                    {
                        "productCode" : "TSHIRT",
                        "type" : "FixedBulkPricePromotion",
                        "config": {
                            "minApplicableQuantity": 3,
                            "discountedUnitPrice": 19.0
                        }
                    }
                ]
            }
        """.trimIndent()
    }
}

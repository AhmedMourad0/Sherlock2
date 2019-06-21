package inc.ahmedmourad.sherlock.viewmodel

import androidx.lifecycle.ViewModel
import inc.ahmedmourad.sherlock.domain.constants.Gender
import inc.ahmedmourad.sherlock.domain.constants.Hair
import inc.ahmedmourad.sherlock.domain.constants.Skin
import inc.ahmedmourad.sherlock.model.*
import inc.ahmedmourad.sherlock.viewmodel.model.DefaultLiveData

class FindChildrenViewModel : ViewModel() {

    val firstName by lazy { DefaultLiveData("") }
    val lastName by lazy { DefaultLiveData("") }

    val skin by lazy { DefaultLiveData(Skin.WHITE) }
    val hair by lazy { DefaultLiveData(Hair.BLONDE) }

    val gender by lazy { DefaultLiveData(Gender.MALE) }

    val location by lazy { DefaultLiveData(AppLocation.empty()) }

    val age by lazy { DefaultLiveData(15) }
    val height by lazy { DefaultLiveData(120) }

    fun toAppChildCriteriaRules() = AppChildCriteriaRules(
            AppName(firstName.value.trim(), lastName.value.trim()),
            location.value,
            AppAppearance(
                    gender.value,
                    skin.value,
                    hair.value,
                    PInt(age.value),
                    PInt(height.value)
            )
    )
}

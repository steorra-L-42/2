package com.kimnlee.common.utils

import com.kimnlee.common.R

object  CarModelImageProvider {
    fun getImageResId(carModel: String): Int {
        return when (carModel) {
            // Audi
            "A3" -> R.drawable.a3
            "A4" -> R.drawable.a4
            "A5" -> R.drawable.a5
            "A6" -> R.drawable.a6
            "A7" -> R.drawable.a7
            "A8" -> R.drawable.a8
            "Q2" -> R.drawable.q2
            "Q3" -> R.drawable.q3
            "Q5" -> R.drawable.q5
            "Q7" -> R.drawable.q7
            "Q8" -> R.drawable.q8
            // Mercedes-Benz
            "A-클래스" -> R.drawable.aclass
            "C-클래스" -> R.drawable.cclass
            "CLA" -> R.drawable.cla
            "CLE" -> R.drawable.cle
            "E-클래스" -> R.drawable.eclass
            "GLC" -> R.drawable.glc
            "GLE" -> R.drawable.gle
            "S-클래스" -> R.drawable.sclass
            // KGM
            "액티언" -> R.drawable.actyon
            "코란도" -> R.drawable.corando
            "렉스턴" -> R.drawable.rexton
            "티볼리" -> R.drawable.tivoli
            "토레스" -> R.drawable.torres
            // HYUNDAI
            "아반떼" -> R.drawable.avante
            "그랜저" -> R.drawable.grandeur
            "아이오닉5" -> R.drawable.ioniq5
            "아이오닉6" -> R.drawable.ioniq6
            "코나" -> R.drawable.kona
            "넥쏘" -> R.drawable.nexo
            "팰리세이드" -> R.drawable.palisade
            "싼타페" -> R.drawable.santafe
            "쏘나타" -> R.drawable.sonata
            "투싼" -> R.drawable.tucson
            "베뉴" -> R.drawable.venue
            // BMW
            "BMW3" -> R.drawable.bmw3
            "BMW5" -> R.drawable.bmw5
            "BMS7" -> R.drawable.bmw7
            "X3" -> R.drawable.x3
            "X5" -> R.drawable.x5
            "X6" -> R.drawable.x6
            // KIA
            "카니발" -> R.drawable.carnival
            "EV3" -> R.drawable.ev3
            "EV6" -> R.drawable.ev6
            "EV9" -> R.drawable.ev9
            "K5" -> R.drawable.k5
            "K8" -> R.drawable.k8
            "K9" -> R.drawable.k9
            "모하비" -> R.drawable.mohave
            "모닝" -> R.drawable.morning
            "니로" -> R.drawable.niro
            "니로EV" -> R.drawable.niroev
            "레이" -> R.drawable.ray
            "셀토스" -> R.drawable.seltos
            "쏘렌토" -> R.drawable.sorento
            "스포티지" -> R.drawable.sportage
            // CHEVROLET
            "콜로라도" -> R.drawable.colorado
            "이쿼녹스" -> R.drawable.equinox
            "임팔라" -> R.drawable.impala
            "말리부" -> R.drawable.malibu
            "스파크" -> R.drawable.spark
            "트레일블레이저" -> R.drawable.trailblazer
            "트랙스" -> R.drawable.trax
            // GENESIS
            "G70" -> R.drawable.g70
            "G80" -> R.drawable.g80
            "GV60" -> R.drawable.gv60
            "GV70" -> R.drawable.gv70
            "GV80" -> R.drawable.gv80
            // TESLA
            "모델3" -> R.drawable.model3
            "모델S" -> R.drawable.models
            "모델X" -> R.drawable.modelx
            "모델Y" -> R.drawable.modely
            // RENAULT
            "QM6" -> R.drawable.qm6
            "SM6" -> R.drawable.sm6
            "XM3" -> R.drawable.xm3
            else -> R.drawable.ghibli
        }
    }
}
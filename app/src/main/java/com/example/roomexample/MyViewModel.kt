package com.example.roomexample


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.roomexample.database.Scene
import com.example.roomexample.database.SceneDatabase
import com.example.roomexample.network.GetService
import com.example.roomexample.network.TemperatureEntity
import com.example.roomexample.network.asTemperatureEntity
import kotlinx.coroutines.launch

//viewmodel need to know the database dao (passed argument here) for accessing data from the database
//class MyViewModel(dataSource: SceneDatabaseDao) : ViewModel(){

//use the AndroidViewModel with default parameter: application context
class MyViewModel(application: Application) : AndroidViewModel(application) {

    //get the reference to the database dao
    private val database = SceneDatabase.getInstance(application).sceneDatabaseDao

    //a list of all scenes or matched scenes from the database (LiveData)
    val sceneList = MediatorLiveData<List<Scene>>()

    //get the selected scene (livedata for easy observing)
    val selectedScene = MutableLiveData<Scene>()

    //get the city weather (livedata)
    val cityWeather = MutableLiveData<TemperatureEntity?>()

    //list of cities in Taiwan
    //the following way is sometime not working: don't know
    val cityList : Array<String> = application.resources.getStringArray(R.array.city_array)

//    val cityList = arrayOf(
//        "花蓮縣", "台東縣", "宜蘭縣", "屏東縣", "台東縣", "高雄市",
//        "雲林縣", "彰化縣", "臺南市", "嘉義縣", "嘉義市", "臺中市", "臺北市",
//        "新北市", "新竹縣", "新竹市", "基隆市", "苗栗縣", "桃園市", "南投縣", "澎湖縣",
//        "金門縣", "連江縣"
//    )

    init {
        getAllScenes()
    }

    fun getAllScenes() {  //set the livedata source of the sceneList to be all scenes
        sceneList.addSource(database.loadAllScenes()) { scenes ->
            sceneList.setValue(scenes)
        }
    }

    fun searchAllScenes(name: String) { //set the livedata source of the sceneList to be matched scenes
        sceneList.addSource(database.findScenes(name)) { scenes ->
            sceneList.setValue(scenes)
        }
    }

    fun getScene(sceneId: Long) {  //get one scene from the sceneList
        sceneList.value?.let {
            selectedScene.value = it.find { it.id == sceneId }
        }
    }

    fun insertScene(newScene: Scene) {  //add a new scene into the database
        viewModelScope.launch {
            database.insertScene(newScene)
        }
    }

    fun updateScene(oldScene: Scene) {  //add a new scene into the database
        viewModelScope.launch {
            database.updateScene(oldScene)
        }
    }

    fun deleteScene(oldScene: Scene) {  //delete a scene from the database
        viewModelScope.launch {
            database.deleteScene(oldScene)
        }
    }

    fun retrieveWeather(location: String) {
        //initially set null
        cityWeather.value = null
        viewModelScope.launch {
            try {
                val result =
                    GetService.retrofitService.getAppData(location)
                cityWeather.value = result.asTemperatureEntity()
            } catch (e: Exception) {
                Log.d("Main", "Fail to access: ${e.message}")
            }
        }
    }

    fun initDB() {  //setup the initial database
        viewModelScope.launch {
            database.insertScene(
                Scene(
                    "花蓮縣",
                    "砂婆礑",
                    "花蓮縣秀林鄉砂婆礑溪",
                    R.drawable.photo1_1,
                    "花蓮踩水烤肉的好景點，不只能烤肉還可以順便玩水、游泳、跳水。",
                    "2020/02/12"

                )
            )
            database.insertScene(
                Scene(
                    "花蓮縣",
                    "清水斷崖",
                    "花蓮縣秀林鄉清水斷崖",
                    R.drawable.photo1_2,
                    "大自然的鬼力，切割出落差達1千多公尺的太魯閣峽谷而聞名。",
                    "2021/02/19"
                )
            )
            database.insertScene(
                Scene(
                    "花蓮縣",
                    "立霧溪",
                    "花蓮縣秀林鄉立霧溪",
                    R.drawable.photo1_3,
                    "大自然的鬼力，切割出落差達1千多公尺的太魯閣峽谷而聞名。",
                    "2020/12/06"
                )
            )

            database.insertScene(
                Scene(
                    "南投縣",
                    "台14甲路邊",
                    "南投縣仁愛鄉仁和路",
                    R.drawable.photo2_1,
                    "回家的路上，偶然遇見的雲海和夕陽造就的美景。",
                    "2019/12/01"
                )
            )
            database.insertScene(
                Scene(
                    "南投縣",
                    "武嶺碑",
                    "南投縣仁愛鄉仁和路170號",
                    R.drawable.photo2_2,
                    "台灣公路的最高點，晴朗時的白天風景遼闊一人，晚上滿天星斗。多雲多霧多雨時，會有平地不曾有的體驗。",
                    "2020/02/11"
                )
            )
            database.insertScene(
                Scene(
                    "高雄縣",
                    "天池",
                    "高雄市桃源區天池",
                    R.drawable.photo3_1,
                    "在我有記憶以來沒有通車過的南橫，目前該公路民眾可抵達的的最高點，我也騎車去過兩三次，自住家來回六小時起跳。",
                    "2020/02/18"
                )
            )
            database.insertScene(
                Scene(
                    "屏東縣",
                    "霧台櫻花王",
                    "屏東縣霧台鄉霧台村",
                    R.drawable.photo3_2,
                    "南部比較知名的山區景點，名產愛玉和小米粥，春天時公路後段有櫻花綻放，這顆櫻花王是當地民眾所有，但很大方的讓遊客觀賞。",
                    "2021/02/08"
                )
            )
            database.insertScene(
                Scene(
                    "宜蘭縣",
                    "蘭陽博物館",
                    "宜蘭縣頭城鎮青雲路三段750號",
                    R.drawable.photo4_1,
                    "在我有記憶以來沒有通車過的南橫，目前該公路民眾可抵達的的最高點，我也騎車去過兩三次，自住家來回六小時起跳。",
                    "2021/02/19"
                )
            )
            database.insertScene(
                Scene(
                    "宜蘭縣",
                    "澳花瀑布",
                    "宜蘭縣南澳鄉澳花瀑布",
                    R.drawable.photo4_2,
                    "美，溪水超漂亮，花蓮才看的到的藍色，但到這裡之前須要走點山路。",
                    "2019/03/02"
                )
            )
        }
    }
}
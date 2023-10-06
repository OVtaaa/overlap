<template>

  <van-tabs v-model:active="active[row]" type="card" shrink
            class="custom-tabs" color="#f8f8f8" title-inactive-color="black" title-active-color="brown"
            v-for="(tagsRow,row) in tags" @change="doChange" >

    <van-tab :title="tagsRow.tagName" :title-style="activeStyle(row,0)"></van-tab>

    <van-tab v-for="(tag,index) in tagsRow.subTag"
             :title="tag.tagName" :title-style="activeStyle(row,index+1)"></van-tab>
  </van-tabs>

<!--  通过激活标签实时获取的用户信息 进行展示-->
  <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <user-card-list :result-user="resultUser"/>
    </van-list>
  </van-pull-refresh>

</template>

<script setup>
import {onMounted, ref} from 'vue';
import myAxios from "../../plugins/myAxios.js";
import qs from "qs";
import userPage from "../../config/userPage.ts";
import UserCardList from "../../components/user/UserCardList.vue";

const tags = ref([])
const active = ref([])
const resultUser = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
let curPageNum = 0
const pageSize = userPage.pageSize

// 根据行数和激活标签的索引值，来设置激活标签的圆角边框
const activeStyle = (row, index) => {
  if (index === active.value[row]) {
    return {
      'border-radius': '16px',
    }
  }
}

// 激活标签改变时，通过其激活索引数组active值 及 标签数组tags 获得当前选中标签发送请求到后端
const doChange = () => {
  curPageNum = 0

  //  根据游戏标签展示段位
  if(active.value[0] !== 0){
    myAxios.get("/user/search/show/tag?tagName="+
        tags.value[0].subTag[active.value[0]-1].tagName)
    .then(function (response) {
      const levels = response.data?.data
      if(levels) {
        tags.value[1].subTag = levels
      }
    })
    .catch(function (error) {
      console.log(error);
    });
  }else {
    tags.value[1].subTag = []
  }

  resultUser.value = []
  finished.value = false
  loading.value = true
  onLoad()
}

onMounted(async () => {
  // 请求展示初始标签列表
  const initTags = await myAxios.get('/user/tag/show/init')
    .then(function (response) {
      return response.data?.data
    })
    .catch(function (error) {
      // 处理错误情况
      console.error(error);
    })

  if(initTags) {
    tags.value = initTags
    // 初始化激活标签索引数组
    for(let i = 0; i < initTags.length; i++)
      active.value.push(0)
  }
})

const onLoad = () => {
  // 若 load 时刷新属性为 true，将列表置空
  if (refreshing.value) {
    resultUser.value = [];
    curPageNum = 0;
    refreshing.value = false;
  }
  curPageNum++
  let param = []
  //  将所选标签赋值给 param 数组
  for(let i = 0; i < active.value.length; i++){
    if(active.value[i] !== 0){
      param.push(tags.value[i].subTag[active.value[i]-1].tagName)
    }
  }
  // 更新数据
  myAxios.get("/user/search/tags", {
    params:{
      tags: param,
      pageNum: curPageNum,
      pageSize: pageSize
    },
    paramsSerializer: params => {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
  .then(function (response) {
    const searchUser = response.data?.data
    if(searchUser.list){
      searchUser.list.forEach(user => {
        if(user.introduction === null)
          user.introduction = "暂无"
      })
      for(let i = 0; i < searchUser.list.length; i++)
        resultUser.value.push( searchUser.list[i] )
    }

    // 加载状态结束
    loading.value = false;
    // 数据全部加载完成
    finished.value = (searchUser.curPageNum * searchUser.pageSize) >= searchUser.total;

    // console.log("RescurPageNum："+searchUser.curPageNum+" curPageNum: "+curPageNum +"\n pageSize："+searchUser.pageSize
    //     +" total："+searchUser.total + " is_finished: "+  finished.value)
  })
  .catch(function (error) {
    console.log(error);
  });
};

const onRefresh = () => {
  finished.value = false;
  // 重新加载数据
  // 将 loading 设置为 true，表示处于加载状态
  loading.value = true;
  onLoad();
};

</script>

<style scoped>
.custom-tabs {
  --van-border-width: 0;
}
</style>
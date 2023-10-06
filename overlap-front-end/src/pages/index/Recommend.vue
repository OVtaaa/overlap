<template>

  <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="min-height: 70vh;">
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
import {ref} from "vue";
import {useRouter} from "vue-router";
import myAxios from "../../plugins/myAxios.js";
import userPage from "../../config/userPage.ts";
import UserCardList from "../../components/user/UserCardList.vue";

const router = useRouter()

const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
let curPageNum = 0
const pageSize = userPage.pageSize
const resultUser = ref([])

const onLoad = () => {
  // 若 load 时刷新属性为 true，将列表置空
  if (refreshing.value) {
    resultUser.value = [];
    curPageNum = 0;
    refreshing.value = false;
  }

  curPageNum++
  // 更新数据
  myAxios.get("/user/recommend", {
    params:{
      pageNum: curPageNum,
      pageSize: pageSize
    }
  })
  .then(function (response) {
    const searchUser = response.data?.data
    if(searchUser && searchUser.list){
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
    finished.value = searchUser
        ? (searchUser.curPageNum * searchUser.pageSize) >= searchUser.total
        : true;
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
</style>
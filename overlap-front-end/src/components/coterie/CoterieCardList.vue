<template>

  <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="min-height: 70vh;">
    <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        class="finished-text-with-line-break"
    >
      <van-card v-for="coterie in resultCoterie"
                :title="coterie.title"
                :thumb="coterie.avatarUrl"
                @click-thumb="toChat(coterie.title,coterie.id,coterie.avatarUrl,1)"
      >
        <template #desc>
          <van-text-ellipsis
              :content="coterie.description"
              expand-text="展开"
              collapse-text="收起"
              style="margin-top: 8px; margin-bottom: 6px"
          />
        </template>
        <template #tags>
          <van-tag v-if="coterie.isEncrypted === 1" round text-color="#ad0000" color="#ffe1e1" style="margin-right: 8px; margin-bottom: 4px; margin-top: 4px">
            加密
          </van-tag>
          <van-tag v-if="coterie.status === 1" round text-color="#ad0000" color="#ffe1e1" style="margin-right: 8px; margin-bottom: 4px; margin-top: 4px">
            私密
          </van-tag>
          <van-tag v-for="val in getTags(coterie.tags)" round text-color="#ad0000" color="#ffe1e1"
                   style="margin-right: 8px; margin-bottom: 4px; margin-top: 4px">
            {{ val }}
          </van-tag>
        </template>
        <template #num>
          <span v-text="`${coterie.curNum} / ${coterie.maxNum}`"></span>
        </template>
        <template #footer>
          <slot name="my-footer-slot" :coterie="coterie"/>
        </template>
      </van-card>
    </van-list>
  </van-pull-refresh>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import coteriePage from "../../config/coteriePage";
import myAxios from "../../plugins/myAxios.js";
import {useRouter} from "vue-router";
import {getCurrentUser} from "../../service/userService";

interface coterieCardListProps {
  path: string,
  isMyCoterie?: boolean,
}

//@ts-ignore
const props = withDefaults(defineProps<coterieCardListProps>(),{
  path: "/user/current",
  isMyCoterie: false,
})


const resultCoterie = ref([])
const router = useRouter()
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
let curPageNum = 0
const pageSize = coteriePage.pageSize
const curUser = ref()

onMounted(async () => {
  curUser.value = await getCurrentUser()
})

const toChat = (title,id,avatarUrl,type) => {
  if(!props.isMyCoterie)
    return
  router.push({
    path: '/chat',
    query: {
      title: title,
      toId: id,
      username: curUser.value.username,
      fromId: curUser.value.id,
      type: type,
      toAvatarUrl: avatarUrl,
      fromAvatarUrl: curUser.value.avatarUrl,
    }
  })
}

const onLoad = () => {
  // 若 load 时刷新属性为 true，将列表置空
  if (refreshing.value) {
    resultCoterie.value = [];
    curPageNum = 0;
    refreshing.value = false;
  }

  curPageNum++

  myAxios.get(props.path, {
    params:{
      pageNum: curPageNum,
      pageSize: pageSize
    }
  })
  .then(function (response) {
    const searchCoterie = response.data?.data
    if(searchCoterie && searchCoterie.list){
      searchCoterie.list.forEach(coterie => {
        if(coterie.description === null)
          coterie.description = ""
      })
      for(let i = 0; i < searchCoterie.list.length; i++)
        resultCoterie.value.push( searchCoterie.list[i] )
    }

    // 加载状态结束
    loading.value = false;
    // 数据全部加载完成
    finished.value = searchCoterie
        ? (searchCoterie.curPageNum * searchCoterie.pageSize) >= searchCoterie.total
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

const getTags = (tags) => {
  if(tags)
    return JSON.parse(tags)
  return null
}

//@ts-ignore
defineExpose({ onRefresh,refreshing })

</script>

<style scoped>

</style>
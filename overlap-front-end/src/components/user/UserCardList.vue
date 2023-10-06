<template>
  <van-card v-for="user in resultUser"
            :title="user.username"
            :thumb="user.avatarUrl"
            @click="contact(user.username,user.id,user.avatarUrl,0)"
  >
    <template #desc>
      <van-text-ellipsis
          rows="2"
          :content="`简介：${user.introduction}`"
          expand-text="展开"
          collapse-text="收起"
          style="margin-top: 4px;"
      />
    </template>
    <template #tags>
      <van-tag v-for="val in getTags(user.tags)" round text-color="#ad0000" color="#ffe1e1"
               style="margin-right: 8px; margin-bottom: 4px; margin-top: 4px">
        {{ val }}
      </van-tag>
    </template>
<!--    <template #footer>-->
<!--      <van-button color="linear-gradient(to right, #ff6034, #ee0a24)"-->
<!--                  size="small" @click="contact(user.username,user.id,user.avatarUrl,0)">-->
<!--        联系-->
<!--      </van-button>-->
<!--    </template>-->
  </van-card>
</template>

<script setup lang="ts">
import {UserType} from "../../models/user";
import {useRouter} from "vue-router";
import {getCurrentUser} from "../../service/userService";
import {onMounted, ref} from "vue";

const router = useRouter()
const curUser = ref()


interface userCardListProps {
  resultUser?: UserType[]
}

//@ts-ignore
const props = withDefaults(defineProps<userCardListProps>(),{
  resultUser: []
})

const getTags = (tags) => {
  if(tags)
    return JSON.parse(tags)
  return null
}

onMounted(async () => {
  curUser.value = await getCurrentUser()
})

const contact = async (title,id,avatarUrl,type) => {
  await router.push({
    path: '/chat',
    query: {
      title: title,
      toId: id,
      fromId: curUser.value.id,
      type: type,
      toAvatarUrl: avatarUrl,
      fromAvatarUrl: curUser.value.avatarUrl,
    }
  })
}

</script>

<style scoped>

</style>
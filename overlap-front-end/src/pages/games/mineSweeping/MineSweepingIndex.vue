<template>

  <NavBarForReturn :title="`扫雷`"/>

  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-radio-group v-model="checked">
          <van-cell title="初级" clickable @click="checked = '1'"
                    :label="`${mineConfig.primary.row} * ${mineConfig.primary.cal} ${mineConfig.primary.mine}雷`">
            <template #right-icon>
              <van-radio name="1" />
            </template>
          </van-cell>
          <van-cell title="中级" clickable @click="checked = '2'"
                    :label="`${mineConfig.middle.row} * ${mineConfig.middle.cal} ${mineConfig.middle.mine}雷`">
            <template #right-icon>
              <van-radio name="2" />
            </template>
          </van-cell>
          <van-cell title="高级" clickable @click="checked = '3'"
                    :label="`${mineConfig.high.row} * ${mineConfig.high.cal} ${mineConfig.high.mine}雷`">
            <template #right-icon>
              <van-radio name="3" />
            </template>
          </van-cell>
          <van-cell title="自定义" clickable @click="checked = '4'">
            <template #right-icon>
              <van-radio name="4" />
            </template>
          </van-cell>
      </van-radio-group>
    </van-cell-group>

    <van-cell-group inset>
      <div v-if="checked === '4'">
        <van-field :label="`高度：${curRow}`">
          <template #input>
            <van-slider :max="mineConfig.maxRowSize" :min="mineConfig.minRowSize" v-model="curRow"
                        bar-height="4px" active-color="#7f3a02"/>
          </template>
        </van-field>
        <van-field :label="`宽度：${curCol}`">
          <template #input>
            <van-slider :max="mineConfig.maxCalSize" :min="mineConfig.minCalSize" v-model="curCol"
                        bar-height="4px" active-color="#7f3a02" />
          </template>
        </van-field>
        <van-field :label="`雷：${curMine} (${Math.floor(curMine / (curCol*curRow) * 100)}%)`">
          <template #input>
            <van-slider :max="curRow*curCol/2" :min="mineConfig.minMineSize" v-model="curMine"
                        bar-height="4px" active-color="#7f3a02" />
          </template>
        </van-field>
      </div>
    </van-cell-group>

    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        开始游戏
      </van-button>
    </div>
  </van-form>



</template>

<script setup>
import NavBarForReturn from "../../../components/bar/NavBarForReturn.vue";
import {mineConfig} from "./mineConfig.ts";
import {ref, watch} from "vue";
import {useRouter} from "vue-router";
import {showFailToast} from "vant";

const router = useRouter()

const checked = ref("3")
const curRow = ref(mineConfig.high.row)
const curCol = ref(mineConfig.high.cal)
const curMine = ref(mineConfig.high.mine)

watch(() => [curRow.value,curCol.value],([newRow,newCol],[oldRow,oldCol]) => {
  curMine.value = Math.min(newCol*newRow/2,curMine.value)
})

const onSubmit = () => {
  showFailToast('暂未开放')
  // router.replace({
  //   path: "/games/mineStar",
  //   query: {
  //     curRow: curRow.value,
  //     curCol: curCol.value,
  //     curMine: curMine.value,
  //   }
  // })
}

</script>

<style scoped>

</style>
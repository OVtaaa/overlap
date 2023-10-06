<template>
  <div class="minesweeper" v-if="isOnMounted">
    <div class="board">
      <div
          v-for="row in curRow"
          :key="row"
          class="row"
      >
        <div
            v-for="col in curCol"
            :key="col"
            class="cell"
            @click="handleCellClick(row-1, col-1)"
        >
          <!-- 根据游戏逻辑显示相应的内容 -->
          <div v-if="isCellRevealed(row-1, col-1)">
            <span v-if="isMine(row-1, col-1)" class="mine-icon">💣</span>
            <span v-else>{{ getAdjacentMineCount(row-1, col-1) }}</span>
          </div>
          <div v-else>
            <!-- 未点击的格子显示标记标志 -->
            <span v-if="isCellFlagged(row-1, col-1)" class="flag-icon">🚩</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const curRow = ref(Number(route.query.curRow))
const curCol = ref(Number(route.query.curCol))
const board = ref([])   //  -1：地雷 正数：雷数量  0：空白
const isUsed = ref([])    //  0:未使用 1：已使用 2：被标记
const curMine = ref(Number(route.query.curMine))
const isOnMounted = ref(false)

// 处理单元格点击事件
const handleCellClick = (row, col) => {
// 根据游戏逻辑处理点击事件，例如揭示单元格，标记单元格等
  isUsed.value[row][col] = 1
}

// 检查单元格是否已揭示
const isCellRevealed = (row, col) => {
// 返回单元格是否已揭示的状态
  return isUsed.value[row][col] === 1
}

// 检查单元格是否为地雷
const isMine = (row, col) => {
// 返回单元格是否为地雷的状态
  return board.value[row][col] === -1
}

// 获取相邻地雷的数量
const getAdjacentMineCount = (row, col) => {
// 返回相邻地雷的数量
  return board.value[row][col]
}

// 检查单元格是否被标记
const isCellFlagged = (row, col) => {
// 返回单元格是否被标记的状态
  return isUsed.value[row][col] === 2
}

// 在组件挂载时执行的逻辑
onMounted(() => {
// 在这里可以执行一些初始化逻辑，例如生成地雷位置等
  for(let i = 0; i < curRow.value; i++){
    const row = []
    for(let j = 0; j < curCol.value; j++){
      row.push(0)
    }
    board.value.push(row)
    isUsed.value.push(Array.from(board.value[i]))
  }

  isOnMounted.value = true
  // for(let i = 0; i < curRow.value; i++){
  //   for(let j = 0; j < curCol.value; j++){
  //     console.log(isUsed.value[i][j] + ' ' + i + ' ' +j)
  //     console.log(board.value[i][j] + ' ' + i + ' ' +j)
  //   }
  // }
})
</script>

<style>
.minesweeper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.board {
  display: inline-block;
}

.row {
  display: flex;
}

.cell {
  width: 40px;
  height: 40px;
  border: 1px solid #000;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
}
</style>
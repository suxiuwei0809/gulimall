<template>
  <el-dialog
    title="采购单详情"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-table
      :data="dataList"
      border
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="id" header-align="center" align="center" label="采购单id"></el-table-column>
      <el-table-column prop="skuId" header-align="center" align="center" label="skuId"></el-table-column>
      <el-table-column prop="skuNum" header-align="center" align="center" label="商品数量"></el-table-column>
      <el-table-column prop="skuPrice" header-align="center" align="center" label="商品价格"></el-table-column>
      <el-table-column prop="wareId" header-align="center" align="center" label="仓库id"></el-table-column>
      <el-table-column prop="status" header-align="center" align="center" label="当前状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == 0">新建</el-tag>
          <el-tag type="info" v-if="scope.row.status == 1">已分配</el-tag>
          <el-tag type="warning" v-if="scope.row.status == 2">已领取</el-tag>
          <el-tag type="success" v-if="scope.row.status == 3">已完成</el-tag>
          <el-tag type="danger" v-if="scope.row.status == 4">有异常</el-tag>
        </template>
      </el-table-column>

    </el-table>
    <div style="margin-top: 20px">
      <el-button @click="done(3)">完成采购</el-button>
      <el-button @click="done(4)">采购失败</el-button>
      <el-button @click="cancel">取消选择</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      id: "",
      dataList: [],
      multipleSelection: []
    }
  },
  methods: {
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
      console.log(this.dataListSelections)
    },
    cancel() {
      this.dataListSelections = [];
    },
    /**
     * 3 成功
     * 4 失败
     * @param status
     */
    done(status) {
      let items = [];
      if (this.dataListSelections.length > 0) {
        for (const selection of this.dataListSelections) {
          let item = {
            itemId: selection.id, status: status, reason: ""
          }
          items.push(item)
        }
      }

      let data = {
        id: this.id,
        items: items
      };
      this.$http({
        url: this.$http.adornUrl(`/ware/purchase/done`),
        method: "post",
        data: this.$http.adornData(data)
      }).then(({data}) => {
        console.log(data);
      });

    },
    init(id) {
      this.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$http({
          url: this.$http.adornUrl(`/ware/purchase/getSkusByPurchaseId/${this.id}`),
          method: "get",
          params: this.$http.adornParams({id})
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.data
          } else {
            this.dataList = [];

          }

        });
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/ware/purchase/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'assigneeId': this.dataForm.assigneeId,
              'assigneeName': this.dataForm.assigneeName,
              'phone': this.dataForm.phone,
              'priority': this.dataForm.priority,
              'status': this.dataForm.status,
              'wareId': this.dataForm.wareId,
              'amount': this.dataForm.amount,
              'createTime': this.dataForm.createTime,
              'updateTime': this.dataForm.updateTime
            })
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    }
  }
}
</script>

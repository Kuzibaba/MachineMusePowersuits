///*
// * Copyright (c) 2021. MachineMuse, Lehjr
// *  All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// *      Redistributions of source code must retain the above copyright notice, this
// *      list of conditions and the following disclaimer.
// *
// *     Redistributions in binary form must reproduce the above copyright notice,
// *     this list of conditions and the following disclaimer in the documentation
// *     and/or other materials provided with the distribution.
// *
// *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package lehjr.numina.common.blockentity;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.Connection;
//import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class NuminaBlockEntity extends BlockEntity {
//    public NuminaBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
//        super(pType, pWorldPosition, pBlockState);
//    }
//
//    @Override
//    public CompoundTag getUpdateTag() {
//        return super.getUpdateTag();
//    }
//
//    @Override
//    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//        super.onDataPacket(net, pkt);
//    }
//
//    //    @Override
////    public void onDataPacket(NetworkManager net, SUpdateBlockEntityPacket pkt) {
////        BlockState state = getLevel().getBlockState(getBlockPos());
////        load(state, pkt.getTag());
////        getLevel().sendBlockUpdated(getBlockPos(), state, state, 3);
////    }
//
//    //    @Nullable
////    @Override
////    public SUpdateBlockEntityPacket getUpdatePacket() {
////        return new SUpdateBlockEntityPacket(this.getBlockPos(), 0, getUpdateTag());
////    }
////
////    @Override
////    public void load(BlockState state, CompoundTag nbt) {
////        super.load(state, nbt);
////    }
////
////    @Override
////    public CompoundTag save(CompoundTag compound) {
////        return super.save(compound);
////    }
//    @Override
//    protected void saveAdditional(CompoundTag pTag) {
//        super.saveAdditional(pTag);
//    }
////
////
////    @Override
////    public void saveToItem(ItemStack pStack) {
////        super.saveToItem(pStack);
////    }
//
//    @Override
//    public void load(CompoundTag pTag) {
//        super.load(pTag);
//    }
//}
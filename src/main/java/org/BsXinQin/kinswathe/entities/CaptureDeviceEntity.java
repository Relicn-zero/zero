@Override
public void tick() {
    if (this.getWorld().isClient) return;
    if (this.technicianUUID == null) return;
    
    // 扩大触发范围（可配置，默认1.5格）
    double expandRange = KinsWatheConfig.HANDLER.instance().TechnicianTrapRange;
    if (expandRange <= 0) expandRange = 1.5;
    
    if (this.technicianLifeTime <= KinsWatheConfig.HANDLER.instance().TechnicianCaptureDeviceLifetimeSeconds * 20) {
        this.technicianLifeTime ++;
        List<ServerPlayerEntity> players = this.getWorld().getEntitiesByClass(
            ServerPlayerEntity.class, 
            this.getBoundingBox().expand(expandRange), 
            player -> GameFunctions.isPlayerAliveAndSurvival(player) && !player.getUuid().equals(this.technicianUUID)
        );
        if (!players.isEmpty()) {
            ServerPlayerEntity target = players.get(this.getWorld().random.nextInt(players.size()));
            PlayerEntity technician = this.getWorld().getPlayerByUuid(this.technicianUUID);
            if (target != null) {
                PlayerEffectComponent.KEY.get(target).setStunTicks(KinsWatheConfig.HANDLER.instance().TechnicianCaptureDeviceStunTime * 20);
                TechnicianComponent.KEY.get(target).setCapturedTicks(KinsWatheConfig.HANDLER.instance().TechnicianCaptureDeviceStunTime * 20);
                // 添加缓慢效果（可配置）
                if (KinsWatheConfig.HANDLER.instance().EnableTechnicianSlowness) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2, false, true, true));
                }
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 0.8F, 1.5F);
                if (GameFunctions.isPlayerAliveAndSurvival(technician)) {
                    technician.sendMessage(Text.translatable("tip.kinswathe.technician.captured", target.getName().getString()).withColor(KinsWatheRoles.TECHNICIAN.color()), true);
                    technician.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), SoundCategory.PLAYERS, 1.0F, 1.5F);
                }
            }
            this.discard();
        }
    } else {
        this.discard();
    }
}

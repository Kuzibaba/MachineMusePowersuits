/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package lehjr.numina.common.capabilities.module.powermodule;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IConfig {
    double getBasePropertyDoubleOrDefault(ImmutableList<String> configKey, double baseVal);

    double getTradeoffPropertyDoubleOrDefault(ImmutableList<String> configKey, double multiplier);

    int getTradeoffPropertyIntegerOrDefault(ImmutableList<String> configKey, int multiplier);

    /**
     *
     * @param key provides a path for parsing the config
     *
     *     ImmutableList.of(
     *     "Modules", // modules section of config
     *     categoryTitle, // modules matching category (formatted without whitespace)
     *     moduleName, // unique key derived from descriptionID
     *     "isAllowed" ); // specific config setting
     * @return
     */
    boolean isModuleAllowed(ImmutableList<String> key);

    void setServerConfig(@Nullable ModConfig serverConfig);

    Optional<ModConfig> getModConfig();

    default boolean isLoadingDone() {
        return getModConfig().map(config ->config.getSpec().isCorrecting()).orElse(false);
    }
}
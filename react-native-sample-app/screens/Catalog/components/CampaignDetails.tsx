import { useMemo } from "react";
import { Modal, Text, View } from "react-native";
import { PlaytimeCampaign } from "react-native-adjoe-sdk/src-ts/PlaytimeStudio";
import {
    campaignIsExpired,
    campaignIsInstalled,
    getAllActions,
    getAllMultipliers,
    getBonusActions,
} from "../../../util/campaignsHelper";
import Button from "../../../components/Button";
import SafeArea from "../../../components/SafeArea";
import RewardsSection from "./RewardsSection";
import { spacingStyles, textStyles } from "../../../util/style";
import MultiplierSection from "./MultipliersSection";
import BonusRewardsSection from "./BonusRewardsSection";

type CampaignDetailsProps = {
    clickedCampaign: PlaytimeCampaign | null;
    setClickedCampaign: (campaign: PlaytimeCampaign | null) => void;
};

export default function CampaignDetails({
    clickedCampaign,
    setClickedCampaign,
}: CampaignDetailsProps) {
    const isInstalled = useMemo(() =>
        clickedCampaign ? campaignIsInstalled(clickedCampaign) : false, [clickedCampaign]);

    const isExpired = useMemo(() =>
        clickedCampaign ? campaignIsExpired(clickedCampaign) : false, [clickedCampaign]);

    const rewards = useMemo(() => {
        if (!clickedCampaign) {
            return [];
        }

        const levels = getAllActions(clickedCampaign);
        return levels;
    }, [clickedCampaign]);

    const bonusRewards = useMemo(() => {
        if (!clickedCampaign) {
            return [];
        }

        const levels = getBonusActions(clickedCampaign);
        return levels;
    }, [clickedCampaign]);

    const multipliers = useMemo(() => {
        if (!clickedCampaign) {
            return [];
        }

        const levels = getAllMultipliers(clickedCampaign);
        return levels;
    }, [clickedCampaign]);

    return (
        <Modal
            visible={!!clickedCampaign}
            animationType="fade"
            onRequestClose={() => setClickedCampaign(null)}
        >
            {!!clickedCampaign && (
                <SafeArea>
                    <View style={[spacingStyles.padding]}>
                        <Text style={[textStyles.textBold, textStyles.textLarge, spacingStyles.marginBottom]}>
                            {clickedCampaign.appName}
                        </Text>

                        <RewardsSection
                            levels={rewards}
                            isExpired={isExpired}
                            isInstalled={isInstalled}
                        />

                        {bonusRewards.length ? (
                            <BonusRewardsSection levels={bonusRewards} />
                        ) : null}

                        {multipliers.length ? (
                            <MultiplierSection multipliers={multipliers} />
                        ) : null}

                        <View style={[spacingStyles.marginTop]}>
                            <Button
                                title="Go Back"
                                onPress={() => setClickedCampaign(null)}
                            />
                        </View>
                    </View>
                </SafeArea>
            )}
        </Modal>
    )
}

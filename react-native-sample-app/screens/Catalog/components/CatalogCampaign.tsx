import { useCallback, useEffect, useMemo } from "react";
import { Image, StyleSheet, Text, View, TouchableOpacity } from "react-native";
import { flexStyles, spacingStyles, textStyles } from "../../../util/style";
import { getMaxReward, getTotalReward } from "../../../util/campaignsHelper";
import { PlaytimeCampaign, PlaytimeImpressionType } from "react-native-adjoe-sdk";
import { PlaytimeStudio } from "react-native-adjoe-sdk";
import Button from "../../../components/Button";

type CatalogCampaignProps = {
    campaign: PlaytimeCampaign;
    setClickedCampaign: (campaign: PlaytimeCampaign) => void;
}

const styles = StyleSheet.create({
    shadowContainer: {
        shadowColor: 'rgba(0, 0, 0, 0.2)',
        elevation: 10,
    },
    container: {
        backgroundColor: 'white',
        borderRadius: 12,
    },
    image: {
        height: 170,
        borderTopLeftRadius: 12,
        borderTopRightRadius: 12,
        width: 'auto',
    },
    coinsContainer: {
        borderRadius: 12,
        borderWidth: 1,
        padding: 12,
    },
});

export default function CatalogCampaign({ campaign, setClickedCampaign }: CatalogCampaignProps) {
    const isTimeBased = useMemo(() => campaign.campaignType === 'offerwall', [campaign]);
    const totalReward = useMemo(() => getTotalReward(campaign), [campaign]);
    const maxReward = useMemo(() => getMaxReward(campaign), [campaign]);

    const onPlayNowClicked = useCallback(async () => {
        try {
            await PlaytimeStudio.openInStore(campaign, true)
        } catch (err) {
            console.error(err);
        }
    }, [campaign]);

    const registerCampaignView = useCallback(async () => {
        try {
            await PlaytimeStudio.executeCampaignImpression(campaign, PlaytimeImpressionType.DEFAULT);
        } catch (err) {
            console.error(err);
        }
    }, [campaign]);

    const registerCampaignClick = useCallback(async () => {
        try {
            await PlaytimeStudio.executeCampaignImpression(campaign, PlaytimeImpressionType.ENGAGED);
            setClickedCampaign(campaign);
        } catch (err) {
            console.error(err);
        }
    }, [campaign]);

    const deeplinkCampaign = useCallback(() => {
        PlaytimeStudio.showCampaignDetails(campaign)
            .catch((err) => console.error(err));
    }, []);

    useEffect(() => {
        registerCampaignView();
    }, []);

    return (
        <TouchableOpacity 
            activeOpacity={1}
            onPress={registerCampaignClick}
            style={[spacingStyles.marginBottom, styles.shadowContainer]}
        >
            <View style={[styles.container]}>
                {!!campaign.image?.landscape && (
                    <Image 
                        style={styles.image}
                        source={{ uri: campaign.image?.landscape }} 
                    />
                )}
                <View style={[spacingStyles.padding]}>
                    <Text style={[textStyles.textBold]}>
                        {campaign.appName}
                    </Text>

                    <Text style={[spacingStyles.marginBottom]}>
                        {campaign?.rewardingExpiresAfter || 0} days
                    </Text>

                    <View style={[flexStyles.row, spacingStyles.marginBottom]}>
                        <View style={[flexStyles.flex1, styles.coinsContainer, spacingStyles.marginRight]}>
                            <Text style={[textStyles.textCenter]}>
                                {maxReward} {isTimeBased ? "max per minute" : "max reward"}
                            </Text>
                        </View>
                        <View style={[flexStyles.flex1, styles.coinsContainer]}>
                            <Text style={[textStyles.textCenter]}>
                                {totalReward} total
                            </Text>
                        </View>
                    </View>

                    <Button title="Play Now" onPress={onPlayNowClicked} />
                    <Button title="Deeplink" onPress={deeplinkCampaign} type="link" />
                </View>
            </View>
        </TouchableOpacity>
    );
}

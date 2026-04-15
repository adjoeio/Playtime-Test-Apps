import moment from "moment";
import { 
    PlaytimeCampaign, 
    PlaytimeCashbackConfig, 
    PlaytimeRewardAction,
    PlaytimeRewardActionMultiplier,
} from "react-native-adjoe-sdk/src-ts/common/PlaytimeStudio";
import { theme } from "./theme";

export const getIsTimeBased = (campaign: PlaytimeCampaign): boolean => 
    campaign.campaignType === 'offerwall';

export const getTotalReward = (campaign: PlaytimeCampaign): number => {
    return campaign.eventConfig?.totalCoinsPossible || 0;
};

export const getRewardedActions = (campaign: PlaytimeCampaign): PlaytimeRewardAction[] => {
    const isTimeBased = getIsTimeBased(campaign);

    if (isTimeBased) {
        return campaign.eventConfig?.timeBasedActions.filter((it) => !!it.rewardedAt) || [];
    }

    return campaign.eventConfig?.sequentialActions.filter((it) => !!it.rewardedAt) || [];
}

export const getRewardedCoins = (campaign: PlaytimeCampaign): number => {
    return campaign.eventConfig?.totalCoinsCollected || 0;
};

export const getMaxReward = (campaign: PlaytimeCampaign): number => {
    const isTimeBased = getIsTimeBased(campaign);

    if (isTimeBased) {
        return campaign.eventConfig?.timeBasedActions.find((d) => d.playDuration === 60)?.amount || 0;
    }

    const sequentialActions = campaign.eventConfig?.sequentialActions?.map((v) => v.amount) || [];

    return sequentialActions.length ? Math.max(...sequentialActions) : 0;
};

export const getAllActions = (campaign: PlaytimeCampaign): RewardAction[] => {
    const isTimeBased = getIsTimeBased(campaign);

    if (isTimeBased) {
        return (campaign.eventConfig?.timeBasedActions || []).map((l, i) => ({
            key: i,
            value: l.amount,
            label: `Play ${l.level} seconds`,
            reward: l
        }));
    }

    return (campaign.eventConfig?.sequentialActions || [])
        .filter((l) => !rewardIsTimeBoosted(l))
        .map((l, i) => ({
            key: i,
            value: l.amount,
            label: l.taskDescription,
            reward: l,
        }));
};

export const rewardIsTimeBoosted = (reward: PlaytimeRewardAction) => {
    if (reward.timedCoinsDuration && reward.timedCoinsDuration > 0) {
        return true
    }

    return false;
}

export type RewardAction = {
    key: number;
    value: number;
    label: string;
    reward?: PlaytimeRewardAction;
    cashback?: PlaytimeCashbackConfig;
};

export const campaignIsInstalled = (campaign: PlaytimeCampaign): boolean => {
    return !!campaign.installedAt;
}

export const campaignIsExpired = (campaign: PlaytimeCampaign): boolean => {
    if (campaign.rewardingExpiresAt) {
        return moment(campaign.rewardingExpiresAt).isBefore(moment());
    }
    
    return false;
}

export const getRewardColor = (
    isInstalled: boolean,
    isExpired: boolean,
    reward?: PlaytimeRewardAction, 
    prevReward?: PlaytimeRewardAction,
) => {
    if (isInstalled) {
        if (reward?.rewardedAt) {
            return theme.green
        }

        if (isExpired) {
            return theme.blueGrey;
        }

        if (prevReward) {
            if (prevReward.rewardedAt) {
                return theme.primaryColor;
            }
        }

        if (!prevReward) {
            return theme.primaryColor;
        }
    }

    return theme.blueGrey;
}

export const rewardIsRepeatable = (reward?: PlaytimeRewardAction): boolean => {
    if (reward?.rewardsCount && reward.rewardsCount > 1) {
        return true;
    }
    
    return false;
}

export const getBonusActions = (campaign: PlaytimeCampaign): RewardAction[] => {
    const bonusActionsToReturn: RewardAction[] = [];
    const isTimeBased = getIsTimeBased(campaign);

    const sequentialTimeBoosted: PlaytimeRewardAction[]
        = campaign.eventConfig?.sequentialActions.filter((a) => (
            a.timedCoinsDuration && a.timedCoinsDuration > 0
    )) || []

    const bonusActions: PlaytimeRewardAction[] = campaign.eventConfig?.bonusActions.filter((a) => (
        a.name
    )) || []

    // Append cashback
    if (campaign.eventConfig?.cashbackReward) {
        bonusActionsToReturn.push({ 
            key: 0,
            cashback: campaign.eventConfig?.cashbackReward,
            label: 'Make in-app purchase',
            value: campaign.eventConfig?.cashbackReward?.maxLimitPerCampaignCoins || 0,
        })
    }

    bonusActionsToReturn.push(...[
        // Append time-boosted
        ...(sequentialTimeBoosted.map((a, i): RewardAction => ({
            key: i + 1,
            value: a.amount || 0,
            label: isTimeBased ? `Play ${a.level} seconds` : a.taskDescription,
            reward: a
        }))),

        // Append bonus actions
        ...(bonusActions.map((a, i): RewardAction => ({
            key: i + 1 + sequentialTimeBoosted.length,
            value: a.amount || 0,
            label: a.taskDescription,
            reward: a
        })))
    ])

    return bonusActionsToReturn
}

export const getBonusActionBackground = (reward: RewardAction) => {
    if (reward.cashback) {
        return theme.yellow;
    }
    
    return theme.primaryColor;
}

export const getAllMultipliers = (campaign: PlaytimeCampaign): PlaytimeRewardActionMultiplier[] => {
    return campaign.eventConfig?.multipliersActions || [];
}

export const multiplierIsPending = (multiplier: PlaytimeRewardActionMultiplier) => {
    if (multiplier.status) {
        return true
    }

    return multiplier.status !== "Pending"
}

export const getMultiplierBackground = (multiplier: PlaytimeRewardActionMultiplier) => {
    if (multiplierIsPending(multiplier)) {
        return theme.blueGrey
    }

    return theme.primaryColor
}

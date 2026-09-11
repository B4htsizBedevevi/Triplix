import { defineConfig, type Plugin } from 'vite'
import react from '@vitejs/plugin-react'

const packs={
  fruit:['game-icons:strawberry','game-icons:banana','game-icons:grapes','game-icons:lemon','game-icons:watermelon','game-icons:apple-core','game-icons:carrot','game-icons:mushroom-gills','game-icons:berry-bush','game-icons:flowers','game-icons:sprout','game-icons:pineapple','game-icons:pear','game-icons:peach','game-icons:orange','game-icons:cherry','game-icons:coconut','game-icons:corn'],
  crystal:['game-icons:crystal-cluster','game-icons:crystal-shine','game-icons:topaz','game-icons:minerals','game-icons:crystal-wand','game-icons:crystal-ball','game-icons:diamond','game-icons:emerald','game-icons:ruby','game-icons:sapphire','game-icons:amethyst','game-icons:gem','game-icons:ore','game-icons:gold-nuggets','game-icons:gold-bar','game-icons:jewel-crown','game-icons:engagement-ring','game-icons:treasure-map'],
  magic:['game-icons:magic-potion','game-icons:crystal-ball','game-icons:crystal-wand','game-icons:spell-book','game-icons:wizard-staff','game-icons:fairy-wand','game-icons:portal','game-icons:magic-swirl','game-icons:rune-stone','game-icons:treasure-map','game-icons:enchanted-shield','game-icons:potion-ball','game-icons:scroll-unfurled','game-icons:wizard-hat','game-icons:cauldron','game-icons:magic-hat','game-icons:crystal-eye','game-icons:glowing-hands'],
  space:['game-icons:rocket','game-icons:astronaut-helmet','game-icons:moon','game-icons:sun','game-icons:planet-core','game-icons:saturn','game-icons:ufo','game-icons:alien-stare','game-icons:meteor','game-icons:comet-spark','game-icons:galaxy','game-icons:space-shuttle','game-icons:satellite','game-icons:space-suit','game-icons:asteroid','game-icons:telescope','game-icons:star-cycle','game-icons:solar-system'],
  friends:['game-icons:cat','game-icons:dog','game-icons:fox','game-icons:rabbit','game-icons:bear-face','game-icons:owl','game-icons:frog','game-icons:butterfly','game-icons:fish','game-icons:bird','game-icons:hedgehog','game-icons:squirrel','game-icons:mouse','game-icons:panda','game-icons:penguin','game-icons:turtle','game-icons:wolf-head','game-icons:paw-heart'],
  ancient:['game-icons:ankh','game-icons:scarab-beetle','game-icons:sphinx','game-icons:pyramid','game-icons:greek-temple','game-icons:laurel-crown','game-icons:amphora','game-icons:hourglass','game-icons:hieroglyph-y','game-icons:roman-toga','game-icons:stone-tablet','game-icons:egyptian-walk','game-icons:pharaoh','game-icons:mummy-head','game-icons:ancient-ruins','game-icons:coliseum','game-icons:gorgon','game-icons:medusa-head'],
  nature:['game-icons:sprout','game-icons:flowers','game-icons:tree','game-icons:mountain','game-icons:volcano','game-icons:leaf','game-icons:acorn','game-icons:pine-tree','game-icons:river','game-icons:water-drop','game-icons:fire','game-icons:wind','game-icons:cloud','game-icons:snowflake','game-icons:sun','game-icons:rain','game-icons:rainbow','game-icons:mushroom-gills'],
  season:['game-icons:snowflake','game-icons:falling-leaf','game-icons:blossom','game-icons:sun','game-icons:cloud','game-icons:rain','game-icons:rainbow','game-icons:wind','game-icons:icicles-aura','game-icons:autumn-leaf','game-icons:spring','game-icons:summer','game-icons:falling-star','game-icons:thunderstorm','game-icons:mist','game-icons:temperature-hot','game-icons:temperature-cold','game-icons:weather-vane'],
  collection:['game-icons:compass','game-icons:crown','game-icons:treasure-map','game-icons:key','game-icons:chest','game-icons:gem','game-icons:trophy','game-icons:coin-purse','game-icons:star-medal','game-icons:dice-six-faces','game-icons:target','game-icons:scroll-unfurled','game-icons:medal','game-icons:gold-stack','game-icons:lock','game-icons:map-marker','game-icons:crystal-trophy','game-icons:laurel-trophy'],
}

const triplixIconPackPlugin:Plugin={
  name:'triplix-icon-packs',
  transform(code,id){
    if(!id.endsWith('/web/src/main.tsx')&&!id.endsWith('/src/main.tsx'))return null
    const packSource=`const pack=${JSON.stringify(packs).replace(/"([a-zA-Z]+)":/g,'$1:').replace(/"game-icons:/g,'\'game-icons:').replace(/"/g,'\'')}`
    const safePack=packSource.replace(/\\'/g,"'")
    let next=code.replace(/const pack=.*? as const\n/,safePack+' as const\n')
    next=next.replace(/const kinds=Math\.min\(6,3\+Math\.floor\(\(safe-1\)\/15\)\)/,'const kinds=Math.min(18,Math.ceil(stones/3))')
    return next===code?null:{code:next,map:null}
  },
}

export default defineConfig({
  plugins: [triplixIconPackPlugin,react()],
  base: './',
})

# Libraries

| Name                     | Description |
|--------------------------|-------------|
| *libwaifu_cli*         | RPC client functionality used by *waifu-cli* executable |
| *libwaifu_common*      | Home for common functionality shared by different executables and libraries. Similar to *libwaifu_util*, but higher-level (see [Dependencies](#dependencies)). |
| *libwaifu_consensus*   | Stable, backwards-compatible consensus functionality used by *libwaifu_node* and *libwaifu_wallet* and also exposed as a [shared library](../shared-libraries.md). |
| *libwaifuconsensus*    | Shared library build of static *libwaifu_consensus* library |
| *libwaifu_kernel*      | Consensus engine and support library used for validation by *libwaifu_node* and also exposed as a [shared library](../shared-libraries.md). |
| *libwaifuqt*           | GUI functionality used by *waifu-qt* and *waifu-gui* executables |
| *libwaifu_ipc*         | IPC functionality used by *waifu-node*, *waifu-wallet*, *waifu-gui* executables to communicate when [`--enable-multiprocess`](multiprocess.md) is used. |
| *libwaifu_node*        | P2P and RPC server functionality used by *waifud* and *waifu-qt* executables. |
| *libwaifu_util*        | Home for common functionality shared by different executables and libraries. Similar to *libwaifu_common*, but lower-level (see [Dependencies](#dependencies)). |
| *libwaifu_wallet*      | Wallet functionality used by *waifud* and *waifu-wallet* executables. |
| *libwaifu_wallet_tool* | Lower-level wallet functionality used by *waifu-wallet* executable. |
| *libwaifu_zmq*         | [ZeroMQ](../zmq.md) functionality used by *waifud* and *waifu-qt* executables. |

## Conventions

- Most libraries are internal libraries and have APIs which are completely unstable! There are few or no restrictions on backwards compatibility or rules about external dependencies. Exceptions are *libwaifu_consensus* and *libwaifu_kernel* which have external interfaces documented at [../shared-libraries.md](../shared-libraries.md).

- Generally each library should have a corresponding source directory and namespace. Source code organization is a work in progress, so it is true that some namespaces are applied inconsistently, and if you look at [`libwaifu_*_SOURCES`](../../src/Makefile.am) lists you can see that many libraries pull in files from outside their source directory. But when working with libraries, it is good to follow a consistent pattern like:

  - *libwaifu_node* code lives in `src/node/` in the `node::` namespace
  - *libwaifu_wallet* code lives in `src/wallet/` in the `wallet::` namespace
  - *libwaifu_ipc* code lives in `src/ipc/` in the `ipc::` namespace
  - *libwaifu_util* code lives in `src/util/` in the `util::` namespace
  - *libwaifu_consensus* code lives in `src/consensus/` in the `Consensus::` namespace

## Dependencies

- Libraries should minimize what other libraries they depend on, and only reference symbols following the arrows shown in the dependency graph below:

<table><tr><td>

```mermaid

%%{ init : { "flowchart" : { "curve" : "basis" }}}%%

graph TD;

waifu-cli[waifu-cli]-->libwaifu_cli;

waifud[waifud]-->libwaifu_node;
waifud[waifud]-->libwaifu_wallet;

waifu-qt[waifu-qt]-->libwaifu_node;
waifu-qt[waifu-qt]-->libwaifuqt;
waifu-qt[waifu-qt]-->libwaifu_wallet;

waifu-wallet[waifu-wallet]-->libwaifu_wallet;
waifu-wallet[waifu-wallet]-->libwaifu_wallet_tool;

libwaifu_cli-->libwaifu_util;
libwaifu_cli-->libwaifu_common;

libwaifu_common-->libwaifu_consensus;
libwaifu_common-->libwaifu_util;

libwaifu_kernel-->libwaifu_consensus;
libwaifu_kernel-->libwaifu_util;

libwaifu_node-->libwaifu_consensus;
libwaifu_node-->libwaifu_kernel;
libwaifu_node-->libwaifu_common;
libwaifu_node-->libwaifu_util;

libwaifuqt-->libwaifu_common;
libwaifuqt-->libwaifu_util;

libwaifu_wallet-->libwaifu_common;
libwaifu_wallet-->libwaifu_util;

libwaifu_wallet_tool-->libwaifu_wallet;
libwaifu_wallet_tool-->libwaifu_util;

classDef bold stroke-width:2px, font-weight:bold, font-size: smaller;
class waifu-qt,waifud,waifu-cli,waifu-wallet bold
```
</td></tr><tr><td>

**Dependency graph**. Arrows show linker symbol dependencies. *Consensus* lib depends on nothing. *Util* lib is depended on by everything. *Kernel* lib depends only on consensus and util.

</td></tr></table>

- The graph shows what _linker symbols_ (functions and variables) from each library other libraries can call and reference directly, but it is not a call graph. For example, there is no arrow connecting *libwaifu_wallet* and *libwaifu_node* libraries, because these libraries are intended to be modular and not depend on each other's internal implementation details. But wallet code is still able to call node code indirectly through the `interfaces::Chain` abstract class in [`interfaces/chain.h`](../../src/interfaces/chain.h) and node code calls wallet code through the `interfaces::ChainClient` and `interfaces::Chain::Notifications` abstract classes in the same file. In general, defining abstract classes in [`src/interfaces/`](../../src/interfaces/) can be a convenient way of avoiding unwanted direct dependencies or circular dependencies between libraries.

- *libwaifu_consensus* should be a standalone dependency that any library can depend on, and it should not depend on any other libraries itself.

- *libwaifu_util* should also be a standalone dependency that any library can depend on, and it should not depend on other internal libraries.

- *libwaifu_common* should serve a similar function as *libwaifu_util* and be a place for miscellaneous code used by various daemon, GUI, and CLI applications and libraries to live. It should not depend on anything other than *libwaifu_util* and *libwaifu_consensus*. The boundary between _util_ and _common_ is a little fuzzy but historically _util_ has been used for more generic, lower-level things like parsing hex, and _common_ has been used for waifu-specific, higher-level things like parsing base58. The difference between util and common is mostly important because *libwaifu_kernel* is not supposed to depend on *libwaifu_common*, only *libwaifu_util*. In general, if it is ever unclear whether it is better to add code to *util* or *common*, it is probably better to add it to *common* unless it is very generically useful or useful particularly to include in the kernel.


- *libwaifu_kernel* should only depend on *libwaifu_util* and *libwaifu_consensus*.

- The only thing that should depend on *libwaifu_kernel* internally should be *libwaifu_node*. GUI and wallet libraries *libwaifuqt* and *libwaifu_wallet* in particular should not depend on *libwaifu_kernel* and the unneeded functionality it would pull in, like block validation. To the extent that GUI and wallet code need scripting and signing functionality, they should be get able it from *libwaifu_consensus*, *libwaifu_common*, and *libwaifu_util*, instead of *libwaifu_kernel*.

- GUI, node, and wallet code internal implementations should all be independent of each other, and the *libwaifuqt*, *libwaifu_node*, *libwaifu_wallet* libraries should never reference each other's symbols. They should only call each other through [`src/interfaces/`](`../../src/interfaces/`) abstract interfaces.

## Work in progress

- Validation code is moving from *libwaifu_node* to *libwaifu_kernel* as part of [The libwaifukernel Project #24303](https://github.com/waifu/waifu/issues/24303)
- Source code organization is discussed in general in [Library source code organization #15732](https://github.com/waifu/waifu/issues/15732)
